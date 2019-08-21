package cn.bdqfork.rpc.remote;

import cn.bdqfork.rpc.config.ProtocolConfig;
import cn.bdqfork.rpc.remote.invoker.Invoker;

/**
 * @author bdq
 * @since 2019-08-21
 */
public interface ProviderServerFactory {
    ProviderServer createProviderServer(ProtocolConfig protocolConfig, Invoker<RpcResponse> invoker);
}
