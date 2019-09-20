package cn.bdqfork.common.exception;

/**
 * @author bdq
 * @since 2019-02-27
 */
public class TimeoutException extends RpcException {
    public TimeoutException(String message) {
        super(message);
    }
}
