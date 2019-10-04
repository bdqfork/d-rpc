package cn.bdqfork.rpc.cluster;

import cn.bdqfork.rpc.Directory;
import cn.bdqfork.common.Invoker;

/**
 * @author bdq
 * @since 2019-08-28
 */
public class AvailableCluster implements Cluster {
    @Override
    public <T> Invoker<T> join(Directory<T> directory) {
        return new AvailableClusterInvoker<>(directory);
    }
}
