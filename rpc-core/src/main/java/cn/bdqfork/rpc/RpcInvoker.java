package cn.bdqfork.rpc;

import cn.bdqfork.common.constant.Const;
import cn.bdqfork.common.exception.ConnectionLostException;
import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.common.exception.TimeoutException;
import cn.bdqfork.common.extension.ExtensionLoader;
import cn.bdqfork.common.util.IdUtils;
import cn.bdqfork.rpc.registry.URL;
import cn.bdqfork.rpc.remote.RemoteClient;
import cn.bdqfork.rpc.remote.RpcResponse;
import cn.bdqfork.rpc.remote.context.DefaultFuture;
import cn.bdqfork.rpc.remote.context.RpcContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author bdq
 * @since 2019-02-28
 */
public class RpcInvoker<T> implements Invoker<T> {
    private static final Logger log = LoggerFactory.getLogger(RpcInvoker.class);
    private List<Filter> filters = ExtensionLoader.getExtensions(Filter.class);
    private Class<T> serviceInterface;
    private RemoteClient[] remoteClients;
    private URL url;
    private long timeout;
    private int retries;
    private int count = 0;

    public RpcInvoker(Class<T> type, URL url) {
        this.url = url;
        this.serviceInterface = type;
        sortFilters();
        setTimeoutConfig();
    }

    private void sortFilters() {
        this.filters = this.filters.stream()
                .sorted(Comparator.comparingInt(Filter::getOrder))
                .collect(Collectors.toList());
    }

    private void setTimeoutConfig() {
        timeout = Long.parseLong(url.getParameter(Const.TIMEOUT_KEY, "1000"));
        retries = Integer.parseInt(url.getParameter(Const.RETRY_KEY, "3"));
    }

    @Override
    public RpcResponse invoke(Invocation invocation) throws RpcException {
        RpcInvocation rpcInvocation = (RpcInvocation) invocation;

        RpcContext rpcContext = new RpcContext(IdUtils.getUUID());
        Method method = rpcInvocation.getMethod();
        rpcContext.setUrl(url);
        rpcContext.setMethodName(method.getName());
        rpcContext.setParameterTypes(method.getParameterTypes());

        rpcContext.setArguments(rpcInvocation.getArgs());

        rpcContext.setServiceInterface(serviceInterface.getName());

        String refName = url.getParameter(Const.REF_NAME_KEY);
        rpcContext.setRefName(refName);

        rpcInvocation.setRpcContext(rpcContext);

        log.debug("filter entry ...");
        filters.forEach(filter -> filter.entry(invocation.getRpcContext()));

        RpcResponse result = doInvoke(invocation);

        log.debug("filter after ...");
        filters.forEach(filter -> filter.after(invocation.getRpcContext(), result));

        return result;
    }

    public void setServiceInterface(Class<T> serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    @Override
    public Class<T> getInterface() {
        return serviceInterface;
    }

    @Override
    public boolean isAvailable() {
        for (RemoteClient remoteClient : remoteClients) {
            if (remoteClient.isRunning()) {
                return true;
            }
        }
        return false;
    }

    private RpcResponse doInvoke(Invocation invocation) throws RpcException {
        DefaultFuture defaultFuture = new DefaultFuture();
        RpcContext rpcContext = invocation.getRpcContext();

        DefaultFuture.addFuture(rpcContext.getRequestId(), defaultFuture);

        int retryCount = 0;
        while (true) {
            RemoteClient client;
            try {
                client = remoteClients[count++ % remoteClients.length];
                client.send(rpcContext);
            } catch (ConnectionLostException e) {
                log.warn(e.getMessage());
            }

            try {
                return defaultFuture.get(timeout);
            } catch (TimeoutException e) {
                retryCount = retry(retryCount);

                if (retryCount > retries) {
                    DefaultFuture.remove(rpcContext.getRequestId());
                    throw e;
                }
            }
        }
    }

    private int retry(int retryCount) {

        retryCount++;
        int delayTime = 1000 * retryCount;

        if (retryCount <= retries) {
            log.warn("failed to invoke method , will retry after {} second !", delayTime);
        }
        try {
            Thread.sleep(delayTime);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }

        return retryCount;
    }

    @Override
    public URL getUrl() {
        return url;
    }

    public void setRemoteClients(RemoteClient[] remoteClients) {
        this.remoteClients = remoteClients;
    }

}
