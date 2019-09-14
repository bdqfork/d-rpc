package cn.bdqfork.rpc;

import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.rpc.exporter.Exporter;
import cn.bdqfork.rpc.registry.URL;
import cn.bdqfork.rpc.remote.Invoker;

/**
 * @author bdq
 * @since 2019/9/13
 */
public interface Protocol {
    <T> Exporter export(Invoker<T> invoker);

    <T> Invoker<T> getInvoker(T proxy, Class<T> type, URL url) throws RpcException;
}
