package cn.bdqfork.rpc.protocol;

import cn.bdqfork.common.URL;
import cn.bdqfork.common.extension.Adaptive;
import cn.bdqfork.common.extension.SPI;

/**
 * @author bdq
 * @since 2019-08-21
 */
@SPI("rpc")
public interface RpcServerFactory {
    @Adaptive
    RpcServer getServer(URL url);
}
