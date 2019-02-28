package cn.bdqfork.rpc.registry;

import java.util.Map;

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

    private Map<String, String> parameterMap;

    public URL(String protocol, String host, int port, String path, Map<String, String> parameterMap) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.path = path;
        this.parameterMap = parameterMap;
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

    public String getParameter(String key, String defaultValue) {
        String value = parameterMap.get(key);
        return value == null ? defaultValue : value;
    }

    public String buildString() {
        return protocol + "://" + path + buildParameter();
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

}
