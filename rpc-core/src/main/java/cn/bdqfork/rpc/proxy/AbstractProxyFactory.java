package cn.bdqfork.rpc.proxy;

/**
 * @author bdq
 * @since 2019-03-02
 */
public abstract class AbstractProxyFactory implements ProxyFactory {

    protected Class<?> serviceInterface;
    protected String refName;


}
