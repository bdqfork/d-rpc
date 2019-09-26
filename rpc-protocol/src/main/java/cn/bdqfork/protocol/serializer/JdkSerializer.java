package cn.bdqfork.protocol.serializer;


import cn.bdqfork.rpc.protocol.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * @author bdq
 * @since 2019-02-27
 */
public class JdkSerializer implements Serializer {
    @Override
    public byte[] serialize(Object data) throws Exception {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(data);
        return byteArrayOutputStream.toByteArray();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T deserialize(byte[] data, Class<T> clazz) throws Exception {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        return (T) objectInputStream.readObject();
    }
}
