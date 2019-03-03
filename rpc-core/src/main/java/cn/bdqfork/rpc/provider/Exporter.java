package cn.bdqfork.rpc.provider;


import cn.bdqfork.common.constant.Const;
import cn.bdqfork.rpc.registry.Registry;
import cn.bdqfork.rpc.registry.URL;

import java.util.HashMap;
import java.util.Map;

/**
 * @author bdq
 * @date 2019-03-01
 */
public class Exporter {
    private String host;
    private int port;
    private Registry registry;

    public Exporter(String host, int port, Registry registry) {
        this.host = host;
        this.port = port;
        this.registry = registry;
    }

    public void export(String group, String serviceName, Object instance) {

        LocalRegistry.register(serviceName, instance);

        Map<String, String> map = new HashMap<>(8);
        map.put(Const.GROUP_KEY, group);
        map.put(Const.SIDE_KEY, Const.PROVIDER_SIDE);
        URL url = new URL("provider", host, port, serviceName, map);

        registry.register(url);
    }

}
