package cn.bdqfork.rpc.context.remote;

/**
 * @author bdq
 * @since 2019-09-04
 */
public class Response {
    public static final int CLIENT_ERROR = 100;
    public static final int SERVER_ERROR = 101;
    public static final int TIMEOUT = 90;
    public static final int OK = 200;
    private long id;
    private int status = OK;
    private String message;
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
