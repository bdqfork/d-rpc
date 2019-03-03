package cn.bdqfork.rpc.protocol;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bdq
 * @date 2019-02-21
 */
public class NettyChannel {

    private NettyChannel() {

    }

    private static Map<String, Channel> channelMap = new ConcurrentHashMap<>();

    public static void addChannel(Channel channel) {
        InetSocketAddress socketAddress = (InetSocketAddress) channel.remoteAddress();
        channelMap.put(getAddress(socketAddress.getHostString(), socketAddress.getPort()), channel);
    }

    public static Channel getChannel(String host, Integer port) {
        return channelMap.get(getAddress(host, port));
    }

    public static void removeChannel(Channel channel) {
        InetSocketAddress socketAddress = (InetSocketAddress) channel.remoteAddress();
        channelMap.remove(getAddress(socketAddress.getHostString(), socketAddress.getPort()));
    }

    private static String getAddress(String host, Integer port) {
        return host + ":" + port;
    }

}
