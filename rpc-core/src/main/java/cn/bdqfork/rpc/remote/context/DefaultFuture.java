package cn.bdqfork.rpc.remote.context;

import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.common.exception.TimeoutException;
import cn.bdqfork.rpc.remote.Request;
import cn.bdqfork.rpc.remote.Response;
import cn.bdqfork.rpc.remote.Result;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author bdq
 * @since 2019-02-22
 */
public class DefaultFuture implements RpcFuture<Result> {

    private static Map<Long, DefaultFuture> futureMap = new ConcurrentHashMap<>();

    private ReentrantLock lock = new ReentrantLock();
    private Condition done = lock.newCondition();
    private long id;
    private long timeout;
    private Request request;
    private Result result;

    public DefaultFuture(Request request, long timeout) {
        this.request = request;
        this.id = request.getId();
        this.timeout = timeout;
        futureMap.put(id, this);
    }

    public static void doReceived(Response response) {
        DefaultFuture future = futureMap.get(response.getResponseId());
        if (future != null) {
            future.setResult((Result) response.getData());
            futureMap.remove(response.getResponseId());
        }
    }

    @Override
    public boolean isDone() {
        return result != null;
    }

    @Override
    public Result get() throws RpcException {
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
    public void setResult(Result result) {
        lock.lock();
        try {
            this.result = result;
            done.signal();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void cancle() {
        futureMap.remove(id);
    }

}
