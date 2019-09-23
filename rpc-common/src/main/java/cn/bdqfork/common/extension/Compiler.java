package cn.bdqfork.common.extension;

import javassist.CannotCompileException;
import javassist.NotFoundException;

/**
 * @author bdq
 * @since 2019/9/22
 */
@SPI("javassist")
public interface Compiler {
   Class<?> compile(String className, String code) throws CannotCompileException, NotFoundException;
}
