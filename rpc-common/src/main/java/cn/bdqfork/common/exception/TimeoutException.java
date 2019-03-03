package cn.bdqfork.common.exception;

/**
 * @author bdq
 * @date 2019-02-27
 */
public class TimeoutException extends RpcException {
    public TimeoutException(String message) {
        super(message);
    }
}
