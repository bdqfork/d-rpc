package cn.bdqfork.rpc.protocol;

import cn.bdqfork.common.constant.Const;
import cn.bdqfork.rpc.protocol.netty.server.NettyServer;
import cn.bdqfork.rpc.registry.URL;
import cn.bdqfork.rpc.remote.RpcServer;
import cn.bdqfork.rpc.remote.RpcServerFactory;

/**
 * @author bdq
 * @since 2019-08-21
 */
public class DefaultRpcServerFactory implements RpcServerFactory {

    @Override
    public RpcServer getServer(URL url) {
        String server = url.getProtocol();
        if ("netty".equals(server)) {
            return new NettyServer(url);
        }
        return null;
    }
}
