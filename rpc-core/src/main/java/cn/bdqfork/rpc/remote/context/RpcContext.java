package cn.bdqfork.rpc.remote.context;


import cn.bdqfork.rpc.registry.URL;

import java.io.Serializable;

/**
 * @author bdq
 * @since 2019-03-01
 */
public class RpcContext implements Serializable {
    private URL url;

    private String requestId;

    private String serviceInterface;

    private String refName;

    private String methodName;

    private Class<?>[] parameterTypes;

    private Object[] arguments;

    public RpcContext(String requestId) {
        this.requestId = requestId;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getServiceInterface() {
        return serviceInterface;
    }

    public void setServiceInterface(String serviceInterface) {
        this.serviceInterface = serviceInterface;
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
}
