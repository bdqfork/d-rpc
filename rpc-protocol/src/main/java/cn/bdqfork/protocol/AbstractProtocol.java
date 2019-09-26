package cn.bdqfork.protocol;

import cn.bdqfork.common.Node;
import cn.bdqfork.common.URL;
import cn.bdqfork.common.constant.Const;
import cn.bdqfork.rpc.Exporter;
import cn.bdqfork.rpc.Invoker;
import cn.bdqfork.rpc.protocol.Protocol;
import cn.bdqfork.rpc.protocol.RpcExporter;
import cn.bdqfork.rpc.protocol.RpcServer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bdq
 * @since 2019/9/26
 */
public abstract class AbstractProtocol implements Protocol {
    protected static final Map<String, RpcServer> rpcServerMap = new ConcurrentHashMap<>();

    @Override
    public <T> Exporter export(Invoker<T> invoker) {
        URL url = invoker.getUrl();
        String side = url.getParameter(Const.SIDE_KEY);
        if (Const.PROVIDER_SIDE.equals(side)) {
            doExport(invoker);
        }
        return new RpcExporter(invoker);
    }

    protected abstract <T> void doExport(Invoker<T> invoker);

    @Override
    public <T> Invoker<T> refer(Class<T> type, URL url) {
        return new AsyncToSyncInvoker<>(getBindInvoker(type, url));
    }

    protected abstract <T> Invoker<T> getBindInvoker(Class<T> type, URL url);

    @Override
    public void destory() {
        rpcServerMap.values().forEach(Node::destroy);
    }
}
