package cn.bdqfork.rpc.protocol;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author bdq
 * @since 2019-09-04
 */
public class Request {
    private static final String HEARTBEAT_INFO = null;
    private static final AtomicLong INVOKER_ID = new AtomicLong(0);
    private long id;
    private boolean event;
    private Object data;

    public static long newId() {
        return INVOKER_ID.getAndIncrement();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isEvent() {
        return event;
    }

    public void setEvent(boolean event) {
        this.event = event;
    }

    public void setEvent(String event) {
        this.event = true;
        this.data = event;
    }

    public boolean isHeartbeat() {
        return event && HEARTBEAT_INFO == this.data;
    }

    public void setHeartbeat(boolean event) {
        if (event) {
            this.setEvent(HEARTBEAT_INFO);
        }
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
