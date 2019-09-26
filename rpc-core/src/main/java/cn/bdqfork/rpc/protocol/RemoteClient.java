package cn.bdqfork.rpc.protocol;

import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.rpc.context.DefaultFuture;

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
    DefaultFuture send(Object data) throws RpcException;

    /**
     * 关闭连接
     */
    void close();

    void setHost(String host);

    void setPort(int port);

    void setTimeout(long timeout);

    boolean isRunning();
}
