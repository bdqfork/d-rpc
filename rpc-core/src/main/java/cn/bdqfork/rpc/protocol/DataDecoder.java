package cn.bdqfork.rpc.protocol;

import cn.bdqfork.rpc.common.constant.Const;
import cn.bdqfork.rpc.protocol.invoker.Invocation;
import cn.bdqfork.rpc.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author bdq
 * @date 2019-02-27
 */
public class DataDecoder extends ByteToMessageDecoder {
    private Serializer serializer;

    public DataDecoder(Serializer serializer) {
        this.serializer = serializer;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        byte type = in.readByte();
        int length = in.readableBytes();
        byte[] data = new byte[length];
        in.readBytes(data);
        if (Const.REQUEST_FLAGE == type) {
            Invocation invocation = serializer.deserialize(data, Invocation.class);
            out.add(invocation);
        } else if (Const.RESPOSE_FLAGE == type) {
            RpcResponse rpcResponse = serializer.deserialize(data, RpcResponse.class);
            out.add(rpcResponse);
        }
    }
}
