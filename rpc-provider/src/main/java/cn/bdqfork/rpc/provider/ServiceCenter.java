package cn.bdqfork.rpc.provider;


import cn.bdqfork.rpc.invoker.Invoker;
import cn.bdqfork.rpc.invoker.Provider;
import cn.bdqfork.rpc.netty.RpcResponse;
import cn.bdqfork.rpc.netty.server.NettyServer;

/**
 * @author bdq
 * @date 2019-02-15
 */
public class ServiceCenter implements Server {
    private LocalRegistry localRegistry;
    private Invoker<RpcResponse> invoker;
    private boolean isRunning;
    private String host;
    private int port;

    public ServiceCenter(String host, int port, LocalRegistry localRegistry) {
        this.host = host;
        this.port = port;
        this.localRegistry = localRegistry;
    }

    @Override
    public void stop() {
        isRunning = false;
    }

    @Override
    public void start() {
        NettyServer nettyServer = new NettyServer(host, port, invoker);
        nettyServer.start();
    }

    @Override
    public void register(String serviceInterface, Object impl) {
        localRegistry.register(serviceInterface, impl);
    }

    @Override
    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public int getPort() {
        return port;
    }

    public void setInvoker(Invoker<RpcResponse> invoker) {
        this.invoker = invoker;
    }
}
