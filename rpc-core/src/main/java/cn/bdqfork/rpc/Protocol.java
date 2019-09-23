package cn.bdqfork.rpc;

import cn.bdqfork.common.URL;

/**
 * @author bdq
 * @since 2019/9/13
 */
public interface Protocol {
    <T> Exporter export(Invoker<T> invoker);

    <T> Invoker<T> getInvoker(T proxy, Class<T> type, URL url);

    <T> Invoker<T> refer(Class<T> type, URL url);

}
