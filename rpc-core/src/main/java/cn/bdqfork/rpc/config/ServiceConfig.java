package cn.bdqfork.rpc.config;

import cn.bdqfork.rpc.config.annotation.Service;

/**
 * @author bdq
 * @date 2019-03-03
 */
public class ServiceConfig {
    private String group;
    private String serviceName;
    private String refName;

    public static ServiceConfig build(Service service) {
        ServiceConfig serviceConfig = new ServiceConfig();
        serviceConfig.setGroup(service.group());
        serviceConfig.setServiceName(service.serviceInterface().getName());
        serviceConfig.setRefName(service.refName());
        return serviceConfig;
    }

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
