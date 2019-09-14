package cn.bdqfork.rpc.remote;

import cn.bdqfork.rpc.registry.URL;

/**
 * @author bdq
 * @since 2019-08-21
 */
public interface RpcServerFactory {
    RpcServer getServer(URL url);
}
