package cn.bdqfork.rpc.exporter;

/**
 * @author bdq
 * @since 2019-03-05
 */
public interface Exporter {

    /**
     * 注册服务
     *
     * @param applicationName
     * @param group
     * @param serviceName
     * @param refName
     */
    void export(String applicationName, String group, String serviceName, String refName);
}
