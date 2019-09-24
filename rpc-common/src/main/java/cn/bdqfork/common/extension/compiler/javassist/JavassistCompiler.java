package cn.bdqfork.common.extension.compiler.javassist;


import cn.bdqfork.common.extension.compiler.AbstractCompiler;
import javassist.CtClass;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author bdq
 * @since 2019/9/22
 */
public class JavassistCompiler extends AbstractCompiler {
    private static final Pattern IMPORT_PATTERN = Pattern.compile("(?<=import).*?(?=;)");

    private static final Pattern IMPLEMENTS_PATTERN = Pattern.compile("(?<=implements).*?(?=\\{)");

    private static final Pattern METHODS_PATTERN = Pattern.compile("\n(public|private|protected)\\s+");

    @Override
    protected Class<?> doCompile(String fullClassName, String code) throws Throwable {
        CtClassBuilder builder = CtClassBuilder.builder(fullClassName);

        Matcher matcher = IMPORT_PATTERN.matcher(code);
        while (matcher.find()) {
            String importString = getImportPackage(matcher.group().trim());
            builder.addImport(importString);
        }

        matcher = IMPLEMENTS_PATTERN.matcher(code);
        if (matcher.find()) {
            builder.addImplement(matcher.group().trim());
        }

        String body = code.substring(code.indexOf("{") + 1, code.length() - 1);

        String[] methods = METHODS_PATTERN.split(body);

        Arrays.stream(methods)
                .map(String::trim)
                .filter(method -> !method.isEmpty())
                .forEach(method -> {
                    method = "public " + method;
                    builder.addMethod(method);
                });

        ClassLoader classLoader = getClass().getClassLoader();
        CtClass cls = builder.build(classLoader);
        return cls.toClass(classLoader, JavassistCompiler.class.getProtectionDomain());
    }

    private String getImportPackage(String importString) {
        return importString.substring(0, importString.lastIndexOf("."));
    }
}
