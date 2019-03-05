package cn.bdqfork.rpc.exporter;

/**
 * @author bdq
 * @date 2019-03-05
 */
public interface Exporter {

    void export(String applicationName, String group, String serviceName, String refName);
}
