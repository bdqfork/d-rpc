package cn.bdqfork.rpc.registry.util;

import cn.bdqfork.common.constant.Const;
import cn.bdqfork.common.util.NetUtils;
import cn.bdqfork.rpc.config.RegistryConfig;
import cn.bdqfork.common.URL;

import java.util.List;

/**
 * @author bdq
 * @since 2019/9/18
 */
public class RegistryUtils {
    public static URL buildRegistryURL(RegistryConfig registryConfig) {
        URL url = new URL(registryConfig.getProtocol(), NetUtils.getIp(), 0, "");
        url.addParameter(Const.REGISTRY_KEY, registryConfig.getAddress());
        url.addParameter(Const.SEESION_TIMEOUT_KEY, registryConfig.getSessionTimeout());
        url.addParameter(Const.CONNECTION_TIMEOUT_KEY, registryConfig.getConnectionTimeout());
        url.addParameter(Const.USERNAME_KEY, registryConfig.getUsername());
        url.addParameter(Const.PASSWORD_KEY, registryConfig.getPassword());
        return url;
    }

}
