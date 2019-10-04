package cn.bdqfork.rpc.proxy;

import cn.bdqfork.common.constant.Const;
import cn.bdqfork.common.extension.Adaptive;
import cn.bdqfork.common.extension.SPI;
import cn.bdqfork.rpc.Invoker;
import cn.bdqfork.common.URL;

/**
 * @author bdq
 * @since 2019-08-26
 */
@SPI(JavassistProxyFactory.NAME)
public interface ProxyFactory {

    @Adaptive({Const.PROXY_KEY})
    <T> T getProxy(Invoker<T> invoker);

    @Adaptive({Const.PROXY_KEY})
    <T> Invoker<T> getInvoker(T proxy, Class<T> type, URL url);
}
