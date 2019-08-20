package cn.bdqfork.rpc.registry.event;

/**
 * @author bdq
 * @since 2019-03-01
 */
public enum NodeEvent {
    NONE("none"), CHANGED("changed");
    private final String name;

    NodeEvent(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }}
