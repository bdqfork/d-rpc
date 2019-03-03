package cn.bdqfork.rpc.provider;


import cn.bdqfork.rpc.protocol.invoker.Invoker;
import cn.bdqfork.rpc.protocol.RpcResponse;
import cn.bdqfork.rpc.provider.server.NettyServer;

/**
 * @author bdq
 * @date 2019-02-15
 */
public class ServiceCenter implements Server {
    private String host;
    private int port;
    private Exporter exporter;
    private Invoker<RpcResponse> invoker;
    private boolean isRunning;

    public ServiceCenter(String host, int port, Exporter exporter) {
        this.host = host;
        this.port = port;
        this.exporter = exporter;
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
    public void register(String group, String serviceInterface, Object impl) {
        exporter.export(group, serviceInterface, impl);
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
