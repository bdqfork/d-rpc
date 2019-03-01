package cn.bdqfork.rpc.consumer.invoker;

import cn.bdqfork.common.constant.Const;
import cn.bdqfork.common.exception.RpcException;
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
    private String group = Const.DEFAULT_GROUP;
    private int i;
    private long timeout;
    private int retryTime;

    public LocalInvoker(Exchanger exchanger, long timeout, int retryTime) {
        this.exchanger = exchanger;
        this.timeout = timeout;
        this.retryTime = retryTime;
    }

    @Override
    public Object invoke(Invocation invocation) throws RpcException {
        int retryCount = 0;
        while (true) {
            String serviceName = invocation.getServiceInterface();

            List<NettyClient> nettyClients = exchanger.getNettyClients(group, serviceName);

            //负载均衡
            if (i == Integer.MAX_VALUE) {
                i = 0;
            }

            NettyClient client = nettyClients.get(i++ % nettyClients.size());

            try {
                client.send(invocation);
            } catch (RpcException e) {
                exchanger.removeNettyClient(serviceName, client);
            }

            DefaultFuture defaultFuture = new DefaultFuture(invocation.getRequestId());
            try {
                return defaultFuture.get(timeout);
            } catch (TimeoutException e) {
                retryCount = retry(retryCount, e);
            }
        }
    }

    private int retry(int retryCount, RpcException e) throws RpcException {
        retryCount++;
        int delayTime = 1000 * retryCount;
        if (retryCount <= retryTime) {
            log.warn("failed to invoke method , will retry after {} second !", delayTime);
        } else {
            throw e;
        }
        delay(delayTime);
        return retryCount;
    }

    private void delay(int delayTime) {
        try {
            Thread.sleep(delayTime);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }

    public void setGroup(String group) {
        this.group = group;
    }
}
