package cn.bdqfork.rpc.cluster;

import cn.bdqfork.rpc.remote.Invoker;
import cn.bdqfork.rpc.remote.Directory;

/**
 * @author bdq
 * @since 2019-08-28
 */
public interface Cluster {
   <T> Invoker<T> join(Directory<T> directory);
}
