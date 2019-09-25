package cn.bdqfork.rpc.context;

import cn.bdqfork.rpc.Exporter;
import cn.bdqfork.rpc.registry.Registry;
import cn.bdqfork.common.URL;
import cn.bdqfork.rpc.Invoker;

import java.util.List;

/**
 * @author bdq
 * @since 2019-03-01
 */
public class RpcExporter implements Exporter {
    private Invoker invoker;

    public RpcExporter(Invoker invoker) {
        this.invoker = invoker;
    }

    @Override
    public void undoExport() {
        invoker.destroy();
    }

    @Override
    public Invoker getInvoker() {
        return invoker;
    }

}
