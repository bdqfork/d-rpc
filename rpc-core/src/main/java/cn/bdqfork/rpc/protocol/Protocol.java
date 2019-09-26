package cn.bdqfork.rpc.protocol;

import cn.bdqfork.common.URL;
import cn.bdqfork.common.extension.Adaptive;
import cn.bdqfork.common.extension.SPI;
import cn.bdqfork.rpc.Exporter;
import cn.bdqfork.rpc.Invoker;

/**
 * @author bdq
 * @since 2019/9/13
 */
@SPI("rpc")
public interface Protocol {
    @Adaptive
    <T> Exporter export(Invoker<T> invoker);

    @Adaptive
    <T> Invoker<T> refer(Class<T> type, URL url);

    void destory();

}
