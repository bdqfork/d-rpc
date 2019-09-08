package cn.bdqfork.rpc.remote;

import cn.bdqfork.rpc.config.ProtocolConfig;

import java.util.List;

/**
 * @author bdq
 * @since 2019-08-21
 */
public interface RpcServerFactory {
    RpcServer createProviderServer(ProtocolConfig protocolConfig, List<Invoker<?>> invokers);
}
