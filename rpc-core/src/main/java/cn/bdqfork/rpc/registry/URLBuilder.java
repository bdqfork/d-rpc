package cn.bdqfork.rpc.registry;

import cn.bdqfork.common.constant.Const;
import cn.bdqfork.rpc.config.ProtocolConfig;

/**
 * @author bdq
 * @date 2019-03-05
 */
public class URLBuilder {
    private URL url;

    public URLBuilder(URL url) {
        this.url = url;
    }

    public static URLBuilder consumerUrl(ProtocolConfig protocolConfig, String serviceName) {
        URL url = new URL(Const.PROTOCOL_CONSUMER, protocolConfig.getHost(), protocolConfig.getPort(), serviceName);
        return new URLBuilder(url);
    }

    public static URLBuilder providerUrl(ProtocolConfig protocolConfig, String serviceName) {
        URL url = new URL(Const.PROTOCOL_PROVIDER, protocolConfig.getHost(), protocolConfig.getPort(), serviceName);
        return new URLBuilder(url);
    }

    public URLBuilder applicationName(String applicationName) {
        url.addParameter(Const.APPLICATION_KEY, applicationName);
        return this;
    }

    public URLBuilder refName(String refName) {
        url.addParameter(Const.REF_NAME_KEY, refName);
        return this;
    }

    public URLBuilder group(String group) {
        url.addParameter(Const.GROUP_KEY, group);
        return this;
    }

    public URLBuilder side(String side) {
        url.addParameter(Const.SIDE_KEY, side);
        return this;
    }

    public URL getUrl() {
        return url;
    }
}
