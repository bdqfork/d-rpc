package cn.bdqfork.rpc.context.remote;

/**
 * @author bdq
 * @since 2019/9/16
 */
public interface SerializerFactory {
    Serializer getSerializer(String serialization);
}
