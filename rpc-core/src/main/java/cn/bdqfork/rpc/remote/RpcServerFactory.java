package cn.bdqfork.rpc.remote;

import cn.bdqfork.rpc.Invoker;
import cn.bdqfork.rpc.config.ProtocolConfig;

import java.util.List;
import java.util.Map;

/**
 * @author bdq
 * @since 2019-08-21
 */
public interface RpcServerFactory {
    RpcServer createProviderServer(ProtocolConfig protocolConfig, List<Invoker<?>> invokers);
}
