package cn.bdqfork.rpc.context.result;

import cn.bdqfork.rpc.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

/**
 * @author bdq
 * @since 2019/9/18
 */
public class AsyncResult extends CompletableFuture<Result> implements Result {
    private static final Logger log = LoggerFactory.getLogger(AsyncResult.class);

    @Override
    public Object getValue() {
        return getResponseResult().getValue();
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof Result) {
            this.complete((Result) value);
        } else {
            ResponseResult responseResult = new ResponseResult();
            responseResult.setValue(value);
            this.complete(responseResult);
        }
    }

    private Result getResponseResult() {
        if (this.isDone()) {
            try {
                return this.get();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return new ResponseResult();
    }

    @Override
    public Throwable getException() {
        return getResponseResult().getException();
    }

    @Override
    public void setException(Throwable throwable) {
        ResponseResult responseResult = new ResponseResult();
        responseResult.setException(throwable);
        this.complete(responseResult);
    }

    @Override
    public boolean hasException() {
        return getResponseResult().hasException();
    }

    @Override
    public String getMessage() {
        return getResponseResult().getMessage();
    }

    public void subcribeTo(CompletableFuture<?> future) {
        future.whenComplete((value, throwable) -> {
            if (throwable != null) {
                this.completeExceptionally(throwable);
            } else {
                this.complete((Result) value);
            }
        });
    }

    public static AsyncResult newDefaultAsyncResult() {
        AsyncResult asyncResult = new AsyncResult();
        asyncResult.setValue(null);
        return asyncResult;
    }

}
