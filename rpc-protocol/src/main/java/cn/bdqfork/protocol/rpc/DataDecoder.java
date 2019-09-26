package cn.bdqfork.protocol.rpc;

import cn.bdqfork.common.constant.Const;
import cn.bdqfork.common.exception.RpcException;
import cn.bdqfork.rpc.Invocation;
import cn.bdqfork.rpc.protocol.Request;
import cn.bdqfork.rpc.protocol.Response;
import cn.bdqfork.rpc.Result;
import cn.bdqfork.rpc.protocol.Serializer;
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
        byte magic = in.readByte();
        if (Const.MAGIC != magic) {
            throw new RpcException("Unkown packet !");
        }
        byte flage = in.readByte();

        long id = in.readLong();

        if (Const.REQUEST_FLAGE == flage) {
            Request request = new Request();
            request.setId(id);

            //skip status
            in.skipBytes(4);

            int length = in.readInt();
            byte[] data = new byte[length];
            in.readBytes(data);

            Invocation invocation = serializer.deserialize(data, Invocation.class);
            request.setData(invocation);

            out.add(request);
        } else if (Const.RESPOSE_FLAGE == flage) {
            Response response = new Response();
            response.setId(id);

            int status = in.readInt();
            response.setStatus(status);

            int length = in.readInt();
            byte[] data = new byte[length];
            in.readBytes(data);

            Result result = serializer.deserialize(data, Result.class);
            response.setData(result);
            response.setMessage(result.getMessage());

            out.add(response);
        }
    }
}
