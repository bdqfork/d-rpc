package cn.bdqfork.rpc.config;

import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.rpc.remote.RpcResponse;
import cn.bdqfork.rpc.remote.context.RpcContext;
import cn.bdqfork.rpc.remote.invoker.Invoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2019-02-28
 */
public class RpcRemoteInvoker implements Invoker<RpcResponse>, BeanClassLoaderAware, ApplicationContextAware {
    private static final Logger log = LoggerFactory.getLogger(RpcRemoteInvoker.class);

    public static final String RPC_REMOTE_INVOKER_BEAN_NAME = "RpcRemoteInvoker";

    private ApplicationContext applicationContext;

    private ClassLoader classLoader;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public RpcResponse invoke(RpcContext.Context context) throws RpcException {
        String serviceInterface = context.getServiceInterface();

        Class<?> clazz;
        try {
            clazz = Class.forName(serviceInterface, false, classLoader);
        } catch (ClassNotFoundException e) {

            log.error(e.getMessage(), e);

            return new RpcResponse(context.getRequestId(), e.getMessage(), e);
        }

        Object instance;
        try {
            if (!StringUtils.isEmpty(context.getRefName())) {
                instance = applicationContext.getBean(context.getRefName(), clazz);
            } else {
                instance = applicationContext.getBean(clazz);
            }
        } catch (NoSuchBeanDefinitionException e) {
            log.error(e.getMessage(), e);
            return new RpcResponse(context.getRequestId(), e.getMessage(), e);
        }

        try {
            Method method = clazz.getMethod(context.getMethodName(), context.getParameterTypes());

            Object result = method.invoke(instance, context.getArguments());

            return new RpcResponse(context.getRequestId(), result);

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {

            log.error(e.getMessage(), e);

            return new RpcResponse(context.getRequestId(), e.getMessage(), e);
        }
    }
}
