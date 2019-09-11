package cn.bdqfork.rpc.cluster;

import cn.bdqfork.common.constant.Const;
import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.rpc.registry.URL;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bdq
 * @since 2019/9/11
 */
public class LoadBalanceFactory {
    private static final Map<String, LoadBalance> cache = new ConcurrentHashMap<>();

    public static LoadBalance getLoadBalance(URL url) throws RpcException {
        String loadBalanceName = url.getParameter(Const.LOADBALANCE_KEY);
        if (cache.containsKey(loadBalanceName)) {
            return cache.get(loadBalanceName);
        }
        LoadBalance loadBalance = null;
        if ("random".equals(loadBalanceName)) {
            loadBalance = new RandomLoadBalance();
        }
        if ("robin".equals(loadBalanceName)) {
            loadBalance = new RoundRobinLoadBalance();
        }
        if (loadBalance == null) {
            throw new RpcException(String.format("Can't find LoadBalance named %s !", loadBalanceName));
        }
        cache.put(loadBalanceName, loadBalance);
        return loadBalance;
    }
}
