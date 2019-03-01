package cn.bdqfork.rpc.netty;

import cn.bdqfork.common.exception.TimeoutException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bdq
 * @date 2019-02-22
 */
public class DefaultFuture {

    private static Map<String, DefaultFuture> futureMap = new ConcurrentHashMap<>();

    private Object result;

    public DefaultFuture(String requestId) {
        futureMap.put(requestId, this);
    }

    public boolean isDone() {
        return result != null;
    }

    public synchronized Object get(long timeout) throws TimeoutException {
        long currentTime = System.currentTimeMillis();
        while (!isDone()) {
            if (!isDone()) {
                try {
                    wait(timeout);
                } catch (InterruptedException e) {
                    break;
                }
            }
            if (isDone()) {
                return result;
            } else {
                break;
            }
        }
        if (!isDone() || System.currentTimeMillis() - currentTime > timeout) {
            throw new TimeoutException("request timeout");
        }
        return result;
    }

    private synchronized void setResult(Object result) {
        this.result = result;
        notify();
    }

    public static void doReceived(RpcResponse rpcResponse) {
        DefaultFuture future = futureMap.get(rpcResponse.getRequestId());
        future.setResult(rpcResponse);
    }
}
