package cn.bdqfork.common.util;

import cn.bdqfork.common.URL;
import cn.bdqfork.common.URLBuilder;
import cn.bdqfork.common.config.RegistryConfig;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author bdq
 * @since 2019/9/25
 */
public class RegistryUtils {
    public static String buildRegistryUrlString(List<RegistryConfig> registryConfigs) {
        return registryConfigs.stream()
                .map(URLBuilder::buildRegistryURL)
                .map(URL::buildString)
                .collect(Collectors.joining(","));
    }
}
