package cn.bdqfork.rpc.remote;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author bdq
 * @since 2019-09-04
 */
public class Request {
    private static final AtomicLong INVOKER_ID = new AtomicLong(0);
    private long id;
    private Object data;

    public static long newId(){
        return INVOKER_ID.getAndIncrement();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
