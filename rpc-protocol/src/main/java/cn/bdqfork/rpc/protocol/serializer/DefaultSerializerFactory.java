package cn.bdqfork.rpc.protocol.serializer;

import cn.bdqfork.rpc.remote.Serializer;
import cn.bdqfork.rpc.remote.SerializerFactory;

/**
 * @author bdq
 * @since 2019/9/16
 */
public class DefaultSerializerFactory implements SerializerFactory {
    @Override
    public Serializer getSerializer(String serialization) {
        if ("jdk".equals(serialization)) {
            return new JdkSerializer();
        }
        if ("hessian".equals(serialization)) {
            return new HessianSerializer();
        }
        return null;
    }
}
