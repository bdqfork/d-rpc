package cn.bdqfork.rpc.protocol;

import cn.bdqfork.rpc.config.ProtocolConfig;
import cn.bdqfork.rpc.protocol.netty.provider.NettyProviderServer;
import cn.bdqfork.rpc.remote.ProviderServer;
import cn.bdqfork.rpc.remote.ProviderServerFactory;
import cn.bdqfork.rpc.remote.RpcResponse;
import cn.bdqfork.rpc.remote.invoker.Invoker;

/**
 * @author bdq
 * @since 2019-08-21
 */
public class DefaultProviderServerFactory implements ProviderServerFactory {
    @Override
    public ProviderServer createProviderServer(ProtocolConfig protocolConfig, Invoker<RpcResponse> invoker) {
        if ("netty".equals(protocolConfig.getServer())) {
            return new NettyProviderServer(protocolConfig, invoker);
        }
        return null;
    }
}
