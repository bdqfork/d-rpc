package cn.bdqfork.rpc.protocol;

import cn.bdqfork.common.constant.Const;
import cn.bdqfork.rpc.Invocation;
import cn.bdqfork.rpc.remote.Request;
import cn.bdqfork.rpc.remote.Response;
import cn.bdqfork.rpc.remote.Result;
import cn.bdqfork.rpc.remote.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author bdq
 * @since 2019-02-27
 */
public class DataDecoder extends ByteToMessageDecoder {
    private Serializer serializer;

    public DataDecoder(Serializer serializer) {
        this.serializer = serializer;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        long requestId = in.readLong();
        byte type = in.readByte();

        if (Const.REQUEST_FLAGE == type) {
            Request request = new Request();
            request.setId(requestId);

            int length = in.readInt();
            byte[] data = new byte[length];
            in.readBytes(data);

            Invocation invocation = serializer.deserialize(data, Invocation.class);
            request.setData(invocation);
            out.add(request);
        } else if (Const.RESPOSE_FLAGE == type) {
            Response response = new Response();
            response.setResponseId(requestId);

            int length = in.readInt();
            byte[] data = new byte[length];
            in.readBytes(data);

            Result result = serializer.deserialize(data, Result.class);
            response.setData(result);
            out.add(response);
        }
    }
}
