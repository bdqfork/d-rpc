package cn.bdqfork.rpc.netty;

import java.io.Serializable;

/**
 * @author bdq
 * @date 2019-02-20
 */
public class RpcResponse implements Serializable {
    private String requestId;
    private Object data;
    private String message;
    private Throwable exception;

    public RpcResponse(String requestId, Object data) {
        this.data = data;
        this.requestId = requestId;
    }

    public RpcResponse(String requestId, String message, Throwable exception) {
        this.exception = exception;
        this.message = message;
        this.requestId = requestId;
    }

    public String getRequestId() {
        return requestId;
    }

    public Object getData() {
        return data;
    }

    public Throwable getException() {
        return exception;
    }

    public String getMessage() {
        return message;
    }
}
