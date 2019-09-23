package cn.bdqfork.rpc.context.remote;

import cn.bdqfork.common.extension.SPI;

/**
 * @author bdq
 * @since 2019-02-19
 */
@SPI("hessian")
public interface Serializer {

    byte[] serialize(Object data) throws Exception;

    <T> T deserialize(byte[] data, Class<T> clazz) throws Exception;

}
