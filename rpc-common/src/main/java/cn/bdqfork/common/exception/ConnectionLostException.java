package cn.bdqfork.common.exception;

/**
 * @author bdq
 * @date 2019-03-02
 */
public class ConnectionLostException extends RpcException {
    public ConnectionLostException(String message) {
        super(message);
    }
}
