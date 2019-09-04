package cn.bdqfork.rpc.protocol;

import cn.bdqfork.common.constant.Const;
import cn.bdqfork.rpc.Invocation;
import cn.bdqfork.rpc.remote.Request;
import cn.bdqfork.rpc.remote.Response;
import cn.bdqfork.rpc.remote.Result;
import cn.bdqfork.rpc.remote.Serializer;
import cn.bdqfork.rpc.remote.context.RpcContext;
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
        if (msg instanceof Request) {
            Request request = (Request) msg;
            out.writeLong(request.getId());
            out.writeByte(Const.REQUEST_FLAGE);

            byte[] data = serializer.serialize(request.getData());
            out.writeInt(data.length);
            out.writeBytes(data);
        } else if (msg instanceof Response) {
            Response response = (Response) msg;
            out.writeLong(response.getResponseId());
            out.writeByte(Const.RESPOSE_FLAGE);
            byte[] data = serializer.serialize(response.getData());
            out.writeInt(data.length);
            out.writeBytes(data);
        }
    }
}
