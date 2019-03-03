package cn.bdqfork.rpc.protocol;

import cn.bdqfork.common.constant.Const;
import cn.bdqfork.rpc.protocol.invoker.Invocation;
import cn.bdqfork.rpc.protocol.serializer.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author bdq
 * @date 2019-02-21
 */
public class DataEncoder extends MessageToByteEncoder<Object> {
    private Serializer serializer;

    public DataEncoder(Serializer serializer) {
        this.serializer = serializer;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        byte[] data = serializer.serialize(msg);
        out.writeInt(data.length + 1);
        if (msg instanceof Invocation) {
            out.writeByte(Const.REQUEST_FLAGE);
        } else if (msg instanceof RpcResponse) {
            out.writeByte(Const.RESPOSE_FLAGE);
        }
        out.writeBytes(data);
    }
}
