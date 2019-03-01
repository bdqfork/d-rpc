package cn.bdqfork.rpc.consumer.invoker;

import cn.bdqfork.common.exception.TimeoutException;
import cn.bdqfork.rpc.consumer.remote.Exchanger;
import cn.bdqfork.rpc.invoker.Invocation;
import cn.bdqfork.rpc.invoker.Invoker;
import cn.bdqfork.rpc.netty.DefaultFuture;
import cn.bdqfork.rpc.netty.client.NettyClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author bdq
 * @date 2019-02-28
 */
public class LocalInvoker implements Invoker<Object> {
    private static final Logger log = LoggerFactory.getLogger(LocalInvoker.class);
    private Exchanger exchanger;
    private int i;
    private long timeout;

    public LocalInvoker(Exchanger exchanger, long timeout) {
        this.exchanger = exchanger;
        this.timeout = timeout;
    }

    @Override
    public Object invoke(Invocation invocation) {
        String serviceName = invocation.getServiceInterface();

        List<NettyClient> nettyClients = exchanger.getNettyClients(serviceName);

        //负载均衡
        if (i == Integer.MAX_VALUE) {
            i = 0;
        }
        NettyClient client = nettyClients.get(i++ % nettyClients.size());

        client.send(invocation);

        DefaultFuture defaultFuture = new DefaultFuture(invocation.getRequestId());
        try {
            return defaultFuture.get(timeout);
        } catch (TimeoutException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}
