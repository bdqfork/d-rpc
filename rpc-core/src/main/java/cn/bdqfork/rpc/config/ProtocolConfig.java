package cn.bdqfork.rpc.config;

/**
 * @author bdq
 * @since 2019-02-28
 */
public class ProtocolConfig {
    /**
     * ip地址
     */
    private String host;
    /**
     * 端口号
     */
    private Integer port;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}
