package cn.bdqfork.rpc.context;


import cn.bdqfork.rpc.Invocation;
import cn.bdqfork.rpc.URL;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * @author bdq
 * @since 2019-03-01
 */
public class RpcContext {
    private static ThreadLocal<RpcContext> threadLocal = ThreadLocal.withInitial(RpcContext::new);

    private URL url;

    private String methodName;

    private Class<?>[] parameterTypes;

    private Object[] arguments;

    private Invocation invocation;

    private Future<?> future;

    private Map<String, Object> attachments = new HashMap<>();

    public static RpcContext getRpcContext() {
        return threadLocal.get();
    }

    public static void remove(RpcContext rpcContext) {
        threadLocal.remove();
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }

    public Invocation getInvocation() {
        return invocation;
    }

    public void setInvocation(Invocation invocation) {
        this.invocation = invocation;
    }

    public Future<?> getFuture() {
        return future;
    }

    public void setFuture(Future<?> future) {
        this.future = future;
    }

    public Map<String, Object> getAttachments() {
        return attachments;
    }

    public void setAttachments(Map<String, Object> attachments) {
        this.attachments = attachments;
    }
}
