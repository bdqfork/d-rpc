package cn.bdqfork.rpc.remote;

import java.io.Serializable;

/**
 * @author bdq
 * @since 2019-02-20
 */
public class Result implements Serializable {
    private Object data;
    private String message;
    private Throwable exception;

    public Result(Object data) {
        this.data = data;
    }

    public Result(String message, Throwable exception) {
        this.exception = exception;
        this.message = message;
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
