package cn.bdqfork.rpc.consumer.context;

import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.common.exception.TimeoutException;
import cn.bdqfork.rpc.netty.RpcResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
