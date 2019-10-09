package cn.bdqfork.rpc.context;

import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.rpc.protocol.Request;
import cn.bdqfork.rpc.protocol.Response;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author bdq
 * @since 2019-02-22
 */
public class DefaultFuture extends CompletableFuture<Object> {
    private static Logger log = LoggerFactory.getLogger(DefaultFuture.class);
    private static Map<Long, DefaultFuture> FUTURES = new ConcurrentHashMap<>();
    private static Timer timer = new HashedWheelTimer(new BasicThreadFactory.Builder()
            .daemon(true)
            .namingPattern("rpc-future-timeout")
            .build(),
            30, TimeUnit.MILLISECONDS);

    private long id;
    private long timeout;
    private Request request;
    private Timeout timeoutCheckTask;

    private DefaultFuture(Request request, long timeout) {
        this.request = request;
        this.id = request.getId();
        this.timeout = timeout;
        FUTURES.put(id, this);
    }

    public static DefaultFuture newFuture(Request request, long timeout) {
        DefaultFuture future = new DefaultFuture(request, timeout);
        doTimeoutCheck(future);
        return future;
    }

    private static void doTimeoutCheck(DefaultFuture future) {
        TimeoutCheckTask task = new TimeoutCheckTask(future.getId());
        future.timeoutCheckTask = future.timer.newTimeout(task, future.timeout, TimeUnit.MILLISECONDS);
    }

    public static void received(Response response) {
        received(response, false);
    }

    public static void received(Response response, boolean timeout) {
        try {
            DefaultFuture future = FUTURES.remove(response.getId());
            if (future != null) {
                if (!timeout) {
                    Timeout timeoutCheckTask = future.timeoutCheckTask;
                    timeoutCheckTask.cancel();
                }
                future.doReceived(response);
            } else {
                log.warn("The timeout request returned !");
            }
        } finally {
            FUTURES.remove(response.getId());
        }
    }

    private void doReceived(Response response) {
        if (response.getStatus() == Response.OK) {
            super.complete(response.getData());
        } else if (response.getStatus() == Response.TIMEOUT) {
            super.completeExceptionally(new TimeoutException(response.getMessage()));
        } else if (response.getStatus() == Response.SERVER_ERROR || response.getStatus() == Response.CLIENT_ERROR) {
            super.completeExceptionally(new RpcException(response.getMessage()));
        }
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        Response errorResult = new Response(id);
        errorResult.setStatus(Response.CLIENT_ERROR);
        errorResult.setMessage("request future has been canceled.");
        doReceived(errorResult);
        FUTURES.remove(id);
        return super.cancel(mayInterruptIfRunning);
    }

    public void cancel() {
        this.cancel(true);
    }

    private static class TimeoutCheckTask implements TimerTask {
        private final Long requestID;

        TimeoutCheckTask(Long requestID) {
            this.requestID = requestID;
        }

        @Override
        public void run(Timeout timeout) throws Exception {
            DefaultFuture future = DefaultFuture.getFuture(requestID);
            if (future == null || future.isDone()) {
                return;
            }
            Response response = new Response(future.getId());
            response.setStatus(Response.TIMEOUT);
            response.setMessage("The request is timeout");

            DefaultFuture.received(response, true);
        }
    }

    private long getId() {
        return id;
    }

    public static DefaultFuture getFuture(long id) {
        return FUTURES.get(id);
    }

}
