package cn.bdqfork.common.config;

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
     * 应用版本
     */
    private String version;
    /**
     * 应用环境，test或者production
     */
    private String environment;
    /**
     * 编译方式，暂时只支持javassist
     */
    private String compiler;

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getCompiler() {
        return compiler;
    }

    public void setCompiler(String compiler) {
        this.compiler = compiler;
    }
}
