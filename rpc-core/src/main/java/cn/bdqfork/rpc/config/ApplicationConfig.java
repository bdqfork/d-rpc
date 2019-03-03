package cn.bdqfork.rpc.config;

/**
 * @author bdq
 * @date 2019-03-02
 */
public class ApplicationConfig {
    private String applicationName;
    private int globalTimeout;

    public String getApplicationName() {
        return applicationName;
    }

    public ApplicationConfig setApplicationName(String applicationName) {
        this.applicationName = applicationName;
        return this;
    }

    public int getGlobalTimeout() {
        return globalTimeout;
    }

    public void setGlobalTimeout(int globalTimeout) {
        this.globalTimeout = globalTimeout;
    }
}
