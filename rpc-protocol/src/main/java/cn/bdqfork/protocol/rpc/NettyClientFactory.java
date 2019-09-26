package cn.bdqfork.protocol.rpc;

import cn.bdqfork.common.URL;
import cn.bdqfork.common.constant.Const;
import cn.bdqfork.rpc.protocol.AbstractRemoteClientFactory;
import cn.bdqfork.rpc.protocol.RemoteClient;
import cn.bdqfork.rpc.protocol.Serializer;

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
