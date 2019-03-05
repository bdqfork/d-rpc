package cn.bdqfork.rpc.proxy;

import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.rpc.protocol.RpcResponse;
import cn.bdqfork.rpc.protocol.invoker.Invoker;
import javassist.util.proxy.MethodHandler;
import javassist.util.proxy.ProxyFactory;
import javassist.util.proxy.ProxyObject;

import java.lang.reflect.Method;

/**
 * @author bdq
 * @date 2019-03-02
 */
public class JavassistProxyInstanceGenerator<T> extends AbstractProxyInstanceGenerator<T> implements MethodHandler {

    public JavassistProxyInstanceGenerator(Invoker<RpcResponse> invoker, Class<?> serviceInterface, String refName) {
        super(invoker, serviceInterface, refName);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T newProxyInstance() throws RpcException {
        ProxyFactory factory = new ProxyFactory();
        factory.setInterfaces(new Class[]{getServiceInterface()});
        Class<?> clazz = factory.createClass();
        ProxyObject proxyObject;
        try {
            proxyObject = (ProxyObject) clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RpcException(e);
        }
        proxyObject.setHandler(this);
        return (T) proxyObject;
    }

    @Override
    public Object invoke(Object self, Method thisMethod, Method proceed, Object[] args) throws Throwable {
        return doInvoke(thisMethod, args);
    }
}
