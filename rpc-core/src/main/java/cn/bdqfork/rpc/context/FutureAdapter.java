package cn.bdqfork.rpc.context;

import java.util.concurrent.*;

/**
 * @author bdq
 * @since 2019/9/18
 */
public class FutureAdapter<V> extends CompletableFuture<V> {
    private CompletableFuture<ResponseResult> future;

    public FutureAdapter(CompletableFuture<ResponseResult> future) {
        this.future = future;
        future.whenComplete((result, throwable) -> {
            if (throwable != null) {
                if (throwable instanceof CompletionException) {
                    throwable = throwable.getCause();
                }
                this.completeExceptionally(throwable);
            } else {
                if (result.getException() != null) {
                    this.completeExceptionally(result.getException());
                } else {
                    this.complete((V) result.getValue());
                }
            }
        });
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return future.cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
        return future.isCancelled();
    }

    @Override
    public boolean isDone() {
        return super.isDone();
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
        return super.get();
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return super.get(timeout,unit);
    }
}
