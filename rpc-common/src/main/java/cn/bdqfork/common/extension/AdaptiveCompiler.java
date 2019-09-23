package cn.bdqfork.common.extension;

import javassist.CannotCompileException;
import javassist.NotFoundException;

/**
 * @author bdq
 * @since 2019/9/23
 */
@Adaptive
public class AdaptiveCompiler implements Compiler {
    private static volatile String DEFAULT_COMPILER;

    public static void setDefaultCompiler(String compiler) {
        DEFAULT_COMPILER = compiler;
    }

    @Override
    public Class<?> compile(String className, String code) throws CannotCompileException, NotFoundException {
        Compiler compiler;
        ExtensionLoader<Compiler> loader = ExtensionLoader.getExtensionLoader(Compiler.class);
        String name = DEFAULT_COMPILER;
        if (name != null) {
            compiler = loader.getExtension(name);
        } else {
            compiler = loader.getDefaultExtension();
        }
        return compiler.compile(className, code);
    }
}
