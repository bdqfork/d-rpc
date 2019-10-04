package cn.bdqfork.rpc.cluster;

import cn.bdqfork.common.constant.Const;
import cn.bdqfork.common.extension.Adaptive;
import cn.bdqfork.common.extension.SPI;
import cn.bdqfork.rpc.Directory;
import cn.bdqfork.common.Invoker;

/**
 * @author bdq
 * @since 2019-08-28
 */
@SPI("failover")
public interface Cluster {
   @Adaptive({Const.CLUSTER_KEY})
   <T> Invoker<T> join(Directory<T> directory);
}
