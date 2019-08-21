package cn.bdqfork.rpc.remote;

/**
 * @author bdq
 * @since 2019-08-21
 */
public interface RemoteClientFactory {
    RemoteClient createRemoteClient(String server, String serialization, String host, Integer port);
}
