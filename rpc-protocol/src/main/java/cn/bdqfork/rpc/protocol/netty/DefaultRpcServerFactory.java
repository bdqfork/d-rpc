package cn.bdqfork.rpc.protocol.netty;

import cn.bdqfork.common.URL;
import cn.bdqfork.rpc.context.remote.RpcServer;
import cn.bdqfork.rpc.context.remote.RpcServerFactory;

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
