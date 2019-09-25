package cn.bdqfork.common.config;

/**
 * @author bdq
 * @since 2019-03-02
 */
public class RegistryConfig {
    /**
     * 注册url
     */
    private String address;
    /**
     *注册中心类型，zookeeper
     */
    private String protocol;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 会话超时时间
     */
    private int sessionTimeout = 3000;
    /**
     * 连接超时时间
     */
    private int connectionTimeout = 3000;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getSessionTimeout() {
        return sessionTimeout;
    }

    public void setSessionTimeout(int sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }
}
