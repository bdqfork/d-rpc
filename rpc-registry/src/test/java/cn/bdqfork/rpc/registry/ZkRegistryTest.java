package cn.bdqfork.rpc.registry;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class ZkRegistryTest {
    private static ZkRegistry registry;

    @BeforeClass
    public static void init() {
        String connectionInfo = "127.0.0.1:2181";
        registry = new ZkRegistry(connectionInfo);
    }

    @Test
    public void register() {
        Map<String, String> map = new HashMap<>();
        map.put("group", "test");
        map.put("side", "server");
        URL url = new URL("rpc", "127.0.0.1", 8080, "UserService", map);
        registry.register(url);
    }

    @Test
    public void subscribe() {
        Map<String, String> map = new HashMap<>();
        map.put("group", "test");
        URL url = new URL("client", "127.0.0.1", 8080, "UserService", map);
        registry.subscribe(url, new EventListener());
    }

    @AfterClass
    public static void close() {
        registry.close();
    }

}