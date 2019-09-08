package cn.bdqfork.rpc.exporter;

import cn.bdqfork.rpc.remote.Invoker;
import cn.bdqfork.rpc.registry.Registry;
import cn.bdqfork.rpc.registry.URL;

/**
 * @author bdq
 * @since 2019-03-01
 */
public class RpcExporter implements Exporter {
    private Invoker invoker;
    private Registry registry;

    public RpcExporter(Invoker invoker) {
        this.invoker = invoker;
    }

    @Override
    public void doExport() {
        URL url = invoker.getUrl();
        registry.register(url);
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

}
