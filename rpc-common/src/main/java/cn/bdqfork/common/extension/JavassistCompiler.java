package cn.bdqfork.common.extension;


import javassist.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author bdq
 * @since 2019/9/22
 */
public class JavassistCompiler implements Compiler {
    private static final Pattern IMPORT_PATTERN = Pattern.compile("(?<=import).*?(?=;)");

    private static final Pattern IMPL_PATTERN = Pattern.compile("(?<=implements).*?(?=\\{)");

    private static final Pattern METHODS_PATTERN = Pattern.compile("\n(public|private|protected)\\s+");

    @Override
    public Class<?> compile(String className, String code) throws CannotCompileException, NotFoundException {
        ClassPool classPool = ClassPool.getDefault();
        Matcher matcher = IMPORT_PATTERN.matcher(code);
        List<String> imports = new ArrayList<>();
        while (matcher.find()) {
            imports.add(matcher.group().trim());
        }
        imports.stream()
                .map(this::getImportPackage)
                .forEach(classPool::importPackage);
        CtClass ctClass = classPool.makeClass(className);

        String impl = null;
        matcher = IMPL_PATTERN.matcher(code);
        if (matcher.find()) {
            impl = matcher.group().trim();
        }
        if (impl != null) {
            ctClass.addInterface(classPool.getCtClass(impl));
        }
        String body = code.substring(code.indexOf("{") + 1, code.length() - 1);
        String[] methods = METHODS_PATTERN.split(body);
        Arrays.stream(methods)
                .map(String::trim)
                .filter(method -> !method.isEmpty())
                .forEach(method -> {
                    try {
                        method = "public " + method;
                        CtMethod ctMethod = CtNewMethod.make(method, ctClass);
                        ctClass.addMethod(ctMethod);
                    } catch (CannotCompileException e) {
                        e.printStackTrace();
                    }
                });
        return ctClass.toClass();
    }

    private String getImportPackage(String importString) {
        return importString.substring(0, importString.lastIndexOf("."));
    }

}
