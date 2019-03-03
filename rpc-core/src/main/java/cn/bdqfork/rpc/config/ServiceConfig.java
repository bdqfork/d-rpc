package cn.bdqfork.rpc.config;

/**
 * @author bdq
 * @date 2019-03-03
 */
public class ServiceConfig {
    private String group;
    private String serviceName;
    private String refName;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getRefName() {
        return refName;
    }

    public void setRefName(String refName) {
        this.refName = refName;
    }
}
