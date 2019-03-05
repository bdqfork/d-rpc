package cn.bdqfork.rpc.config;

import cn.bdqfork.rpc.config.annotation.Reference;
import cn.bdqfork.rpc.protocol.RpcResponse;
import cn.bdqfork.rpc.protocol.invoker.Invoker;

/**
 * @author bdq
 * @date 2019-03-04
 */
public class ReferenceConfig {

    private Reference reference;

    private Invoker<RpcResponse> invoker;

    public static ReferenceConfig build(Reference reference, Invoker<RpcResponse> invoker) {
        ReferenceConfig referenceConfig = new ReferenceConfig();
        referenceConfig.setReference(reference);
        referenceConfig.setInvoker(invoker);
        return referenceConfig;
    }

    public Reference getReference() {
        return reference;
    }

    public void setReference(Reference reference) {
        this.reference = reference;
    }

    public Invoker<RpcResponse> getInvoker() {
        return invoker;
    }

    public void setInvoker(Invoker<RpcResponse> invoker) {
        this.invoker = invoker;
    }

}
