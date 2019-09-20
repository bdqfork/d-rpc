package cn.bdqfork.rpc.context;

import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.common.exception.TimeoutException;
import cn.bdqfork.rpc.context.remote.Request;
import cn.bdqfork.rpc.context.remote.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bdq
 * @since 2019-02-22
 */
public class DefaultFuture extends CompletableFuture<Object> {
    private static Logger log = LoggerFactory.getLogger(DefaultFuture.class);
    private static Map<Long, DefaultFuture> FUTURES = new ConcurrentHashMap<>();

    private long id;
    private long timeout;
    private Request request;
    private Timer timer;

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
        Timer timer = new Timer("timeout", true);
        timer.schedule(task, future.timeout);
        future.timer = timer;
    }

    public static void received(Response response) {
        received(response, false);
    }

    public static void received(Response response, boolean timeout) {
        try {
            DefaultFuture future = FUTURES.remove(response.getId());
            if (future != null) {
                if (!timeout) {
                    Timer timer = future.timer;
                    timer.cancel();
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

    private static class TimeoutCheckTask extends TimerTask {
        private final Long requestID;

        TimeoutCheckTask(Long requestID) {
            this.requestID = requestID;
        }

        @Override
        public void run() {
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
