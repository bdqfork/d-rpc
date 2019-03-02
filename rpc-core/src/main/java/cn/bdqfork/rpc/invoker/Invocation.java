package cn.bdqfork.rpc.invoker;

import cn.bdqfork.common.util.IdUtils;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * @author bdq
 * @date 2019-02-20
 */
public class Invocation implements Serializable {
    private String requestId;
    private String serviceInterface;
    private String refName;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] arguments;

    public Invocation(String requestId, String serviceInterface, String refName, String methodName) {
        this.requestId = requestId;
        this.serviceInterface = serviceInterface;
        this.refName = refName;
        this.methodName = methodName;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getServiceInterface() {
        return serviceInterface;
    }

    public String getRefName() {
        return refName;
    }

    public String getMethodName() {
        return methodName;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public Object[] getArguments() {
        return arguments;
    }
}
