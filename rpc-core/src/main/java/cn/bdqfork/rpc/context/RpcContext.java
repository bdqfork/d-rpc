package cn.bdqfork.rpc.context;


import cn.bdqfork.common.URL;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * @author bdq
 * @since 2019-03-01
 */
public class RpcContext {
    private static ThreadLocal<RpcContext> threadLocal = ThreadLocal.withInitial(RpcContext::new);

    private final Map<String, Object> attachments = new HashMap<>();

    private URL url;

    private String methodName;

    private Class<?>[] parameterTypes;

    private Object[] arguments;

    private InetSocketAddress localAddress;

    private InetSocketAddress remoteAddress;

    private Future<?> future;

    public static RpcContext getRpcContext() {
        return threadLocal.get();
    }

    public static void remove(RpcContext rpcContext) {
        threadLocal.remove();
    }

    public URL getUrl() {
        return url;
    }

    public RpcContext setUrl(URL url) {
        this.url = url;
        return this;
    }

    public String getMethodName() {
        return methodName;
    }

    public RpcContext setMethodName(String methodName) {
        this.methodName = methodName;
        return this;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public RpcContext setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
        return this;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public RpcContext setArguments(Object[] arguments) {
        this.arguments = arguments;
        return this;
    }

    public InetSocketAddress getLocalAddress() {
        return localAddress;
    }

    public RpcContext setLocalAddress(String host, Integer port) {
        this.localAddress = new InetSocketAddress(host, port);
        return this;
    }

    public InetSocketAddress getRemoteAddress() {
        return remoteAddress;
    }

    public RpcContext setRemoteAddress(String host, Integer port) {
        this.remoteAddress = new InetSocketAddress(host, port);
        return this;
    }

    public Future<?> getFuture() {
        return future;
    }

    public RpcContext setFuture(Future<?> future) {
        this.future = future;
        return this;
    }

    public Map<String, Object> getAttachments() {
        return attachments;
    }

    public RpcContext setAttachments(Map<String, Object> attachments) {
        this.attachments.putAll(attachments);
        return this;
    }
}
