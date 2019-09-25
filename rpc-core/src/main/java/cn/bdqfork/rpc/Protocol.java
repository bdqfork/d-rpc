package cn.bdqfork.rpc;

import cn.bdqfork.common.URL;
import cn.bdqfork.common.extension.Adaptive;
import cn.bdqfork.common.extension.SPI;

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
