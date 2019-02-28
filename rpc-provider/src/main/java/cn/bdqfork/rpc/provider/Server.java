package cn.bdqfork.rpc.provider;

/**
 * @author bdq
 * @date 2019-02-15
 */
public interface Server {
    void stop();

    void start();

    void register(String serviceInterface, Object impl);

    boolean isRunning();

    int getPort();
}
