package cn.bdqfork.rpc.remote.context;


import cn.bdqfork.rpc.Invocation;
import cn.bdqfork.rpc.registry.URL;

/**
 * @author bdq
 * @since 2019-03-01
 */
public class RpcContext {
    private static ThreadLocal<RpcContext> threadLocal = ThreadLocal.withInitial(RpcContext::new);

    private URL url;

    private String refName;

    private String methodName;

    private Class<?>[] parameterTypes;

    private Object[] arguments;

    private Invocation invocation;

    public static RpcContext getRpcContext() {
        return threadLocal.get();
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public String getRefName() {
        return refName;
    }

    public void setRefName(String refName) {
        this.refName = refName;
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
}
