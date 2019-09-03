package cn.bdqfork.rpc;

import cn.bdqfork.rpc.remote.context.RpcContext;

import java.io.Serializable;

/**
 * @author bdq
 * @since 2019-08-26
 */
public interface Invocation extends Serializable {
    RpcContext getRpcContext();
}
