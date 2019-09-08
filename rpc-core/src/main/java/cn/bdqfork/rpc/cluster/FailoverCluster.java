package cn.bdqfork.rpc.cluster;

import cn.bdqfork.rpc.remote.Directory;
import cn.bdqfork.rpc.remote.Invoker;

/**
 * @author bdq
 * @since 2019/9/8
 */
public class FailoverCluster implements Cluster {
    @Override
    public <T> Invoker<T> join(Directory<T> directory) {
        return new FailoverClusterInvoker<>(directory);
    }
}
