package cn.bdqfork.protocol.rpc;

import cn.bdqfork.common.URL;
import cn.bdqfork.rpc.protocol.RpcServer;
import cn.bdqfork.rpc.protocol.RpcServerFactory;

/**
 * @author bdq
 * @since 2019-08-21
 */
public class NettyServerFactory implements RpcServerFactory {

    @Override
    public RpcServer getServer(URL url) {
        return new NettyServer(url);
    }
}
