package cn.bdqfork.common.extension;

import cn.bdqfork.common.extension.adaptive.AdaptiveExt;
import cn.bdqfork.common.extension.compiler.javassist.JavassistCompiler;
import org.junit.Test;

import java.lang.reflect.Method;


public class JavassistCompilerTest {

    @Test
    public void compile() {
        Compiler compiler = new JavassistCompiler();
        String code = new AdaptiveClassCodeGenerator(AdaptiveExt.class, "test").generate();
        Class<?> clazz = compiler.compile(code, getClass().getClassLoader());
        System.out.println(AdaptiveExt.class.isAssignableFrom(clazz));
        for (Method method : clazz.getMethods()) {
            System.out.println(method);
        }
    }

}