package cn.bdqfork.rpc.protocol.serializer;

import cn.bdqfork.common.constant.Const;
import cn.bdqfork.rpc.context.remote.*;
import cn.bdqfork.rpc.protocol.netty.NettyClient;
import cn.bdqfork.common.URL;

/**
 * @author bdq
 * @since 2019-08-21
 */
public class DefaultRemoteClientFactory extends AbstractRemoteClientFactory {

    @Override
    protected RemoteClient createRemoteClient(URL url, Serializer serializer) throws IllegalStateException {
        String server = url.getParameter(Const.SERVER_KEY);
        if ("netty".equals(server)) {
            NettyClient nettyClient = new NettyClient(url.getHost(), url.getPort(), serializer);
            long timeout = Long.parseLong(url.getParameter(Const.TIMEOUT_KEY));
            nettyClient.setTimeout(timeout);
            return nettyClient;
        }
        throw new IllegalStateException("No server type " + server + " !");
    }
}
