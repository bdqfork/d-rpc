package cn.bdqfork.rpc.context.remote;

import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.common.URL;

/**
 * @author bdq
 * @since 2019-08-21
 */
public interface RemoteClientFactory {
    RemoteClient[] getRemoteClients(URL url) throws RpcException;
}
