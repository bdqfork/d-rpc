package cn.bdqfork.rpc.consumer.context;

import cn.bdqfork.rpc.common.exception.RpcException;
import cn.bdqfork.rpc.common.exception.TimeoutException;

/**
 * @author bdq
 * @date 2019-02-22
 */
public class DefaultFuture<T> implements RpcFuture<T> {
    private T result;

    @Override
    public boolean isDone() {
        return result != null;
    }

    @Override
    public synchronized T get(long timeout) throws RpcException {
        long currentTime = System.currentTimeMillis();
        if (!isDone()) {
            try {
                wait(timeout);
            } catch (InterruptedException e) {
                throw new RpcException(e);
            }
        }
        if (!isDone() || System.currentTimeMillis() - currentTime > timeout) {
            throw new TimeoutException("request timeout");
        }
        return result;
    }

    @Override
    public synchronized void setResult(T result) {
        this.result = result;
        notify();
    }

}
