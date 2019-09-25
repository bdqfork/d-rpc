package cn.bdqfork.protocol.rpc;

import cn.bdqfork.common.URL;
import cn.bdqfork.rpc.context.remote.RpcServer;
import cn.bdqfork.rpc.context.remote.RpcServerFactory;

/**
 * @author bdq
 * @since 2019-08-21
 */
public class NettyRpcServerFactory implements RpcServerFactory {

    @Override
    public RpcServer getServer(URL url) {
        return new NettyServer(url);
    }
}
