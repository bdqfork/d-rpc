package cn.bdqfork.rpc.context.remote;

/**
 * @author bdq
 * @date 2019-02-19
 */
public interface Serializer {

    byte[] serialize(Object data) throws Exception;

    <T> T deserialize(byte[] data, Class<T> clazz) throws Exception;

}
