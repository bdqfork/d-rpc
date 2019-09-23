package cn.bdqfork.common.extension;

import cn.bdqfork.common.extension.adaptive.AdaptiveExt;
import javassist.CannotCompileException;
import javassist.NotFoundException;
import org.junit.Test;

import java.lang.reflect.Method;


public class JavassistCompilerTest {

    @Test
    public void compile() throws CannotCompileException, NotFoundException {
        String className = "cn.bdqfork.common.extension.adaptive.AdaptiveExt$Adaptive";
        Compiler compiler = new JavassistCompiler();
        String code = new AdaptiveClassCodeGenerator(AdaptiveExt.class, "test").generate();
        Class<?> clazz = compiler.compile(className, code);
        System.out.println(AdaptiveExt.class.isAssignableFrom(clazz));
        for (Method method : clazz.getMethods()) {
            System.out.println(method);
        }
    }

}