package cn.bdqfork.rpc.remote.context;

import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.common.exception.TimeoutException;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author bdq
 * @since 2019-02-22
 */
public class DefaultFuture<T> implements RpcFuture<T> {
    private ReentrantLock lock = new ReentrantLock();
    private Condition done = lock.newCondition();
    private T result;

    @Override
    public boolean isDone() {
        return result != null;
    }

    @Override
    public T get(long timeout) throws RpcException {
        if (!isDone()) {
            long start = System.currentTimeMillis();
            lock.lock();
            try {
                while (!isDone()) {
                    done.await(timeout, TimeUnit.MILLISECONDS);
                    if (isDone() || System.currentTimeMillis() - start > timeout) {
                        break;
                    }
                }
            } catch (InterruptedException e) {
                throw new RpcException(e);
            } finally {
                lock.unlock();
            }
            if (!isDone() || System.currentTimeMillis() - start > timeout) {
                throw new TimeoutException("request timeout");
            }
        }
        return result;
    }

    @Override
    public void setResult(T result) {
        lock.lock();
        try {
            this.result = result;
            done.signal();
        } finally {
            lock.unlock();
        }
    }

}
