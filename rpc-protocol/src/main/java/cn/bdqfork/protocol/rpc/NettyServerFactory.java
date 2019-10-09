package cn.bdqfork.protocol.rpc;

import cn.bdqfork.common.URL;
import cn.bdqfork.common.constant.Const;
import cn.bdqfork.rpc.protocol.RpcServer;
import cn.bdqfork.rpc.protocol.RpcServerFactory;

/**
 * @author bdq
 * @since 2019-08-21
 */
public class NettyServerFactory implements RpcServerFactory {

    @Override
    public RpcServer getServer(URL url) {
        String server = url.getParameter(Const.SERVER_KEY);
        if (NettyServer.NAME.equals(server)) {
            return new NettyServer(url);
        }
        throw new IllegalArgumentException("Failed to create rpc server , unknow server " + server + " !");
    }
}
