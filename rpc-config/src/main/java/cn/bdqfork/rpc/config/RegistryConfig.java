package cn.bdqfork.rpc.config;

/**
 * @author bdq
 * @date 2019-03-02
 */
public class RegistryConfig {
    private String client;
    private String url;
    private String username;
    private String password;

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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
}
