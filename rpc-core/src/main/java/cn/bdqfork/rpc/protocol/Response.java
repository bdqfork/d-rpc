package cn.bdqfork.rpc.protocol;

/**
 * @author bdq
 * @since 2019-09-04
 */
public class Response {
    private static final String HEARTBEAT_INFO = null;
    public static final int CLIENT_ERROR = 100;
    public static final int SERVER_ERROR = 101;
    public static final int TIMEOUT = 90;
    public static final int OK = 200;
    private long id;
    private int status = OK;
    private String message;
    private boolean event;
    private Object data;

    public Response() {
    }

    public Response(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
