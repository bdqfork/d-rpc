package cn.bdqfork.common.extension.compiler.jdk;

import cn.bdqfork.common.extension.compiler.AbstractCompiler;

import javax.tools.*;
import java.io.File;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;

/**
 * @author bdq
 * @since 2019/10/10
 */
public class JdkCompiler extends AbstractCompiler {
    private JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    private StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, StandardCharsets.UTF_8);
    private DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();

    @Override
    protected Class<?> doCompile(String fullClassName, String code) throws Throwable {
        StrSrcJavaObject srcObject = new StrSrcJavaObject(fullClassName, code);
        Iterable<? extends JavaFileObject> fileObjects = Collections.singletonList(srcObject);

        String outDir = getClass().getClassLoader().getResource("").getFile() + File.separator;

        Iterable<String> options = Arrays.asList("-d", outDir);

        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, options, null, fileObjects);
        task.call();

        return Class.forName(fullClassName);
    }

    private static class StrSrcJavaObject extends SimpleJavaFileObject {
        private String content;

        StrSrcJavaObject(String name, String content) {
            super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
            this.content = content;
        }

        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return content;
        }
    }

}
