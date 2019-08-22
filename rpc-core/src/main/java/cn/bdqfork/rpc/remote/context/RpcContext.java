package cn.bdqfork.rpc.remote.context;


import cn.bdqfork.rpc.remote.RpcResponse;

import java.beans.Transient;
import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bdq
 * @since 2019-03-01
 */
public class RpcContext implements Serializable {
    private static Map<String, DefaultFuture> futureMap = new ConcurrentHashMap<>();

    private Context context;

    public RpcContext(String requestId) {
        this.context = new Context(requestId);
    }

    public Context getContext() {
        return context;
    }

    public static void registerContext(String requestId, DefaultFuture future) {
        futureMap.put(requestId, future);
    }

    public static void doReceived(RpcResponse rpcResponse) {
        DefaultFuture future = futureMap.get(rpcResponse.getRequestId());
        if (future != null) {
            future.setResult(rpcResponse);
            futureMap.remove(rpcResponse.getRequestId());
        }
    }

    public static void removeContext(String requestId) {
        futureMap.remove(requestId);
    }

    public class Context implements Serializable {
        private String requestId;
        private String serviceInterface;
        private String refName;
        private String methodName;
        private Class<?>[] parameterTypes;
        private Object[] arguments;
        private DefaultFuture<RpcResponse> future;

        private Context(String requestId) {
            this.requestId = requestId;
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

        public DefaultFuture<RpcResponse> getFuture() {
            return future;
        }

        public void setFuture(DefaultFuture<RpcResponse> future) {
            this.future = future;
        }
    }
}
