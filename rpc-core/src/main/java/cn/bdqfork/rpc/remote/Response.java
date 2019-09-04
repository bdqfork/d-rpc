package cn.bdqfork.rpc.remote;

/**
 * @author bdq
 * @since 2019-09-04
 */
public class Response {
    private long responseId;
    private Object data;

    public long getResponseId() {
        return responseId;
    }

    public void setResponseId(long responseId) {
        this.responseId = responseId;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
