package cn.bdqfork.rpc;

import cn.bdqfork.common.constant.Const;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * rpc://10.20.153.10:1234/barService?param=value
 *
 * @author bdq
 * @since 2019-02-26
 */
public class URL implements Serializable {
    /**
     * 协议名
     */
    private String protocol;
    /**
     * 服务IP
     */
    private String host;
    /**
     * 服务端口
     */
    private int port;
    /**
     * 接口名称
     */
    private String service;

    private Map<String, Object> parameterMap = new ConcurrentHashMap<>();

    public URL(String protocol, String host, int port, String service) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.service = service;
    }

    public URL(URL url) {
        this.protocol = url.getProtocol();
        this.host = url.getHost();
        this.port = url.getPort();
        this.service = url.getServiceName();
        this.parameterMap = url.getParameterMap();
    }

    public URL(String urlString) {

        String[] urlStrings = urlString.split("://");

        protocol = urlStrings[0];

        String[] pathStrings = urlStrings[1].split("/");

        String[] hostPort = pathStrings[0].split(":");

        host = hostPort[0];
        port = Integer.parseInt(hostPort[1]);

        String[] paramString = pathStrings[1].split("\\?");
        service = paramString[0];

        String[] params = paramString[1].split("&");

        for (String param : params) {
            String[] s7 = param.split("=");
            addParameter(s7[0], s7[1]);
        }

    }

    public String toServiceCategory() {
        return "/" + service;
    }

    public String toPath() {
        return toServiceCategory() + "/" + parameterMap.get(Const.SIDE_KEY) + "/" + getAddress();
    }

    public <T> void addParameter(String key, T value) {
        if (value == null) {
            return;
        }
        if (value instanceof String && StringUtils.isBlank((String) value)) {
            return;
        }
        parameterMap.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getParameter(String key) {
        return (T) parameterMap.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T getParameter(String key, T defaultValue) {
        T value = (T) parameterMap.get(key);
        return value == null ? defaultValue : value;
    }

    public Map<String, Object> getParameterMap() {
        return parameterMap;
    }

    public void setParameterMap(Map<String, String> parameterMap) {
        this.parameterMap.putAll(parameterMap);
    }

    public void clear() {
        this.parameterMap.clear();
    }

    public String buildString() {
        return protocol + "://" + host + ":" + port + "/" + service + buildParameter();
    }

    private String buildParameter() {
        if (parameterMap.size() == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, Object> entry : parameterMap.entrySet()) {
            if (first) {
                builder.append("?");
                first = false;
            } else {
                builder.append("&");
            }
            builder.append(entry.getKey())
                    .append("=")
                    .append(entry.getValue());
        }
        return builder.toString();
    }

    public String getAddress() {
        return host + ":" + port;
    }

    public String getServiceName() {
        return service;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        URL url = (URL) o;
        return port == url.port &&
                protocol.equals(url.protocol) &&
                host.equals(url.host) &&
                service.equals(url.service) &&
                parameterMap.equals(url.parameterMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(protocol, host, port, service, parameterMap);
    }
}

