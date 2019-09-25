package cn.bdqfork.rpc.protocol.netty;

import cn.bdqfork.common.URL;
import cn.bdqfork.common.constant.Const;
import cn.bdqfork.rpc.context.remote.AbstractRemoteClientFactory;
import cn.bdqfork.rpc.context.remote.RemoteClient;
import cn.bdqfork.rpc.context.remote.Serializer;
import cn.bdqfork.rpc.protocol.netty.NettyClient;

/**
 * @author bdq
 * @since 2019-08-21
 */
public class NettyClientFactory extends AbstractRemoteClientFactory {

    @Override
    protected RemoteClient createRemoteClient(URL url, Serializer serializer) {
        NettyClient nettyClient = new NettyClient(url.getHost(), url.getPort(), serializer);
        long timeout = Long.parseLong(url.getParameter(Const.TIMEOUT_KEY));
        nettyClient.setTimeout(timeout);
        return nettyClient;
    }
}
