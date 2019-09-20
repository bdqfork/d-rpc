package cn.bdqfork.rpc.context;

import cn.bdqfork.rpc.Exporter;
import cn.bdqfork.rpc.registry.Registry;
import cn.bdqfork.rpc.URL;
import cn.bdqfork.rpc.Invoker;

import java.util.List;

/**
 * @author bdq
 * @since 2019-03-01
 */
public class RpcExporter implements Exporter {
    private Invoker invoker;
    private List<Registry> registries;

    public RpcExporter(Invoker invoker) {
        this.invoker = invoker;
    }

    @Override
    public void doExport() {
        URL url = invoker.getUrl();
        registries.forEach(registry -> registry.register(url));
    }

    @Override
    public void undoExport() {
        URL url = invoker.getUrl();
        registries.forEach(registry -> registry.undoRegister(url));
        invoker.destroy();
    }

    @Override
    public Invoker getInvoker() {
        return invoker;
    }

    public void setRegistries(List<Registry> registries) {
        this.registries = registries;
    }
}
