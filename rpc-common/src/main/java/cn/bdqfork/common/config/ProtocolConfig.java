package cn.bdqfork.common.config;

/**
 * 服务端配置
 *
 * @author bdq
 * @since 2019-02-28
 */
public class ProtocolConfig {
    /**
     * 协议名称
     */
    private String name = "rpc";
    /**
     * ip地址
     */
    private String host;
    /**
     * 端口号
     */
    private Integer port;
    /**
     * transport方式
     */
    private String server = "netty";
    /**
     * 序列化方式
     */
    private String serialization = "hessian";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getSerialization() {
        return serialization;
    }

    public void setSerialization(String serialization) {
        this.serialization = serialization;
    }
}
