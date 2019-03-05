package cn.bdqfork.rpc.registry;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * rpc://10.20.153.10:1234/barService?param=value
 *
 * @author bdq
 * @date 2019-02-26
 */
public class URL {

    private String protocol;

    private String host;

    private int port;

    /**
     * 接口名称
     */
    private String path;

    private Map<String, String> parameterMap = new ConcurrentHashMap<>();

    public URL(String protocol, String host, int port, String path) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.path = path;
    }

    public URL(String urlString) {

        String[] urlStrings = urlString.split("://");

        protocol = urlStrings[0];

        String[] pathStrings = urlStrings[1].split("/");

        String[] hostPort = pathStrings[0].split(":");

        host = hostPort[0];
        port = Integer.parseInt(hostPort[1]);

        String[] paramString = pathStrings[1].split("\\?");
        path = paramString[0];

        String[] params = paramString[1].split("&");

        for (String param : params) {
            String[] s7 = param.split("=");
            addParameter(s7[0], s7[1]);
        }

    }

    public String toServiceCategory() {
        return "/" + path;
    }

    public String toServicePath() {
        return "/" + path + "/" + parameterMap.get("side");
    }

    public String toPath() {
        return toServicePath() + "/" + host + ":" + port;
    }

    public void addParameter(String key, String value) {
        parameterMap.put(key, value);
    }

    public String getParameter(String key, String defaultValue) {
        String value = parameterMap.get(key);
        return value == null ? defaultValue : value;
    }

    public String buildString() {
        return protocol + "://" + host + ":" + port + "/" + path + buildParameter();
    }

    private String buildParameter() {
        if (parameterMap.size() == 0) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : parameterMap.entrySet()) {
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

    public String getServiceName() {
        return path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        URL url = (URL) o;
        return port == url.port &&
                Objects.equals(protocol, url.protocol) &&
                Objects.equals(host, url.host) &&
                Objects.equals(path, url.path) &&
                Objects.equals(parameterMap, url.parameterMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(protocol, host, port, path, parameterMap);
    }

}

