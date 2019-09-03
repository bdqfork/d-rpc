package cn.bdqfork.rpc.remote.context;

import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.common.exception.TimeoutException;
import cn.bdqfork.rpc.remote.RpcResponse;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author bdq
 * @since 2019-02-22
 */
public class DefaultFuture implements RpcFuture<RpcResponse> {

    private static Map<String, DefaultFuture> futureMap = new ConcurrentHashMap<>();

    private ReentrantLock lock = new ReentrantLock();
    private Condition done = lock.newCondition();
    private RpcResponse result;

    public static void addFuture(String requestId, DefaultFuture future) {
        futureMap.put(requestId, future);
    }

    public static void doReceived(RpcResponse rpcResponse) {
        DefaultFuture future = futureMap.get(rpcResponse.getRequestId());
        if (future != null) {
            future.setResult(rpcResponse);
            futureMap.remove(rpcResponse.getRequestId());
        }
    }

    public static void remove(String requestId) {
        futureMap.remove(requestId);
    }

    @Override
    public boolean isDone() {
        return result != null;
    }

    @Override
    public RpcResponse get(long timeout) throws RpcException {
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
    public void setResult(RpcResponse rpcResponse) {
        lock.lock();
        try {
            this.result = rpcResponse;
            done.signal();
        } finally {
            lock.unlock();
        }
    }

}
