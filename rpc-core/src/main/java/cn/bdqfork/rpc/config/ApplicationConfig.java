package cn.bdqfork.rpc.config;

/**
 * @author bdq
 * @since 2019-03-02
 */
public class ApplicationConfig {
    /**
     * 应用名
     */
    private String applicationName;
    /**
     * 全局超时
     */
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
