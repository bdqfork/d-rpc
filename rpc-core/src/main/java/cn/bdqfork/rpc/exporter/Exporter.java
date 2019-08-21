package cn.bdqfork.rpc.exporter;

import cn.bdqfork.rpc.registry.URL;

/**
 * @author bdq
 * @since 2019-03-05
 */
public interface Exporter {

    /**
     * 注册服务
     *
     * @param url
     */
    void export(URL url);
}
