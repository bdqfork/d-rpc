package cn.bdqfork.common;

import cn.bdqfork.common.config.RegistryConfig;
import cn.bdqfork.common.constant.Const;
import cn.bdqfork.common.util.NetUtils;

/**
 * @author bdq
 * @since 2019/9/25
 */
public class URLBuilder {
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
