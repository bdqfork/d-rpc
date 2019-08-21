package cn.bdqfork.rpc.config;

import cn.bdqfork.rpc.config.annotation.Reference;
import cn.bdqfork.rpc.remote.RpcResponse;
import cn.bdqfork.rpc.remote.invoker.Invoker;

/**
 * @author bdq
 * @date 2019-03-04
 */
public class ReferenceInfo {

    private Reference reference;

    private Invoker<RpcResponse> invoker;

    public static ReferenceInfo build(Reference reference, Invoker<RpcResponse> invoker) {
        ReferenceInfo referenceInfo = new ReferenceInfo();
        referenceInfo.setReference(reference);
        referenceInfo.setInvoker(invoker);
        return referenceInfo;
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
