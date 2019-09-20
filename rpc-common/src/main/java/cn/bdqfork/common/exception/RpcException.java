package cn.bdqfork.common.exception;

/**
 * @author bdq
 * @since 2019-02-27
 */
public class RpcException extends Exception {

    public RpcException(String message) {
        super(message);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(Throwable cause) {
        super(cause);
    }
}
