package cn.bdqfork.common.exception;

/**
 * @author bdq
 * @date 2019-02-27
 */
public class RemoteException extends RpcException {

    public RemoteException(String message) {
        super(message);
    }

    public RemoteException(Throwable cause) {
        super(cause);
    }

    public RemoteException(String message, Throwable cause) {
        super(message, cause);
    }
}
