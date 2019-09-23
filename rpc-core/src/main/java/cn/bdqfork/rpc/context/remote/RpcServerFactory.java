package cn.bdqfork.rpc.context.remote;

import cn.bdqfork.common.URL;
import cn.bdqfork.common.constant.Const;
import cn.bdqfork.common.extension.Adaptive;
import cn.bdqfork.common.extension.SPI;

/**
 * @author bdq
 * @since 2019-08-21
 */
@SPI("netty")
public interface RpcServerFactory {
    @Adaptive({Const.SERVER_KEY})
    RpcServer getServer(URL url);
}
