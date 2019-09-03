package cn.bdqfork.rpc.remote;

import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.rpc.registry.URL;

/**
 * @author bdq
 * @since 2019-08-21
 */
public interface RemoteClientFactory {
    RemoteClient createRemoteClient(URL url) throws RpcException;
}
