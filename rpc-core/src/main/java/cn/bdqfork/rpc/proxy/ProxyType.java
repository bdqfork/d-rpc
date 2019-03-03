package cn.bdqfork.rpc.proxy;

/**
 * @author bdq
 * @date 2019-03-02
 */
public enum ProxyType {
    /**
     * 代理类型
     */
    JDK(0), JAVASSIST(1);
    private final int value;

    ProxyType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }}
