package cn.bdqfork.common.extension;

/**
 * @author bdq
 * @since 2019/9/22
 */
@SPI("javassist")
public interface Compiler {
   Class<?> compile(String code, ClassLoader classLoader);
}
