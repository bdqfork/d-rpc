package cn.bdqfork.rpc.context;

import cn.bdqfork.rpc.Result;

/**
 * @author bdq
 * @since 2019/9/18
 */
public class ResponseResult implements Result {
    private Object value;
    private String message;
    private Throwable exception;

    public ResponseResult() {
    }

    public ResponseResult(Object value) {
        this.value = value;
    }

    public ResponseResult(String message, Throwable exception) {
        this.message = message;
        this.exception = exception;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public void setValue(Object value) {
        this.value = value;
    }

    @Override
    public Throwable getException() {
        return exception;
    }

    @Override
    public void setException(Throwable throwable) {
        this.exception = throwable;
        this.message = throwable.getMessage();
    }

    @Override
    public boolean hasException() {
        return exception != null;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
