package cn.bdqfork.rpc.remote;

import cn.bdqfork.common.exception.ConnectionLostException;
import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.rpc.remote.context.DefaultFuture;

/**
 * @author bdq
 * @since 2019-08-21
 */
public interface RemoteClient {
    /**
     * 发送数据
     *
     * @param data
     */
    DefaultFuture send(Object data, long timeout) throws RpcException;

    /**
     * 关闭连接
     */
    void close();

    String getHost();

    Integer getPort();

    boolean isRunning();
}
