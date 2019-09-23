package cn.bdqfork.common.extension.adaptive;

import cn.bdqfork.common.URL;
import cn.bdqfork.common.extension.Adaptive;
import cn.bdqfork.common.extension.SPI;

/**
 * @author bdq
 * @since 2019/9/22
 */
@SPI("adaptive")
public interface AdaptiveExt {

    @Adaptive({"name"})
    void test(URL url) throws Exception;

    void test2() throws Exception;
}
