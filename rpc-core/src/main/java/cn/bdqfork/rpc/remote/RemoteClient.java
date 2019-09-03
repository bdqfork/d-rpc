package cn.bdqfork.rpc.remote;

import cn.bdqfork.common.exception.ConnectionLostException;

/**
 * @author bdq
 * @since 2019-08-21
 */
public interface RemoteClient {
    /**
     * 发送数据
     *
     * @param data
     * @throws ConnectionLostException
     */
    void send(Object data) throws ConnectionLostException;

    /**
     * 关闭连接
     */
    void close();

    String getHost();

    Integer getPort();

    boolean isRunning();
}
