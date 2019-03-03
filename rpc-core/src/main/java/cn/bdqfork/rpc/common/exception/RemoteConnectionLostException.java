package cn.bdqfork.rpc.common.exception;

/**
 * @author bdq
 * @date 2019-03-02
 */
public class RemoteConnectionLostException extends RpcException {
    public RemoteConnectionLostException(String message) {
        super(message);
    }
}
