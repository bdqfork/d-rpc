package cn.bdqfork.rpc.provider;

import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.rpc.protocol.RpcResponse;
import cn.bdqfork.rpc.protocol.invoker.Invocation;
import cn.bdqfork.rpc.protocol.invoker.Invoker;
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
 * @date 2019-02-28
 */
public class RpcRemoteInvoker implements Invoker<RpcResponse>, BeanClassLoaderAware, ApplicationContextAware {
    private static final Logger log = LoggerFactory.getLogger(RpcRemoteInvoker.class);

    public static final String RPC_REMOTE_INVOKER_BEAN_NAME = "RpcRemoteInvoker";

    private ApplicationContext context;

    private ClassLoader classLoader;

    @Override
    public RpcResponse invoke(Invocation invocation) throws RpcException {

        String serviceInterface = invocation.getServiceInterface();

        Class<?> clazz;
        try {
            clazz = Class.forName(serviceInterface, false, classLoader);
        } catch (ClassNotFoundException e) {

            log.error(e.getMessage(), e);

            return new RpcResponse(invocation.getRequestId(), e.getMessage(), e);
        }

        Object instance;
        try {
            if (!StringUtils.isEmpty(invocation.getRefName())) {
                instance = context.getBean(invocation.getRefName(), clazz);
            } else {
                instance = context.getBean(clazz);
            }
        } catch (NoSuchBeanDefinitionException e) {
            log.error(e.getMessage(), e);
            return new RpcResponse(invocation.getRequestId(), e.getMessage(), e);
        }

        try {
            Method method = clazz.getMethod(invocation.getMethodName(), invocation.getParameterTypes());

            Object result = method.invoke(instance, invocation.getArguments());

            return new RpcResponse(invocation.getRequestId(), result);

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {

            log.error(e.getMessage(), e);

            return new RpcResponse(invocation.getRequestId(), e.getMessage(), e);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }
}
