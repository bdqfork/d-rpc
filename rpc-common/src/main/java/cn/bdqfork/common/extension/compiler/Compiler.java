package cn.bdqfork.common.extension.compiler;

import cn.bdqfork.common.extension.SPI;

/**
 * Adaptive扩展类的动态编译器
 *
 * @author bdq
 * @since 2019/9/22
 */
@SPI("javassist")
public interface Compiler {
    Class<?> compile(String code, ClassLoader classLoader);
}
