package cn.bdqfork.common.extension.compiler.javassist;

import javassist.*;
import javassist.LoaderClassPath;

import java.util.LinkedList;
import java.util.List;

/**
 * @author bdq
 * @since 2019/9/24
 */
public class CtClassBuilder {
    private String className;
    private List<String> importStrings;
    private List<String> implementNames;
    private List<String> methods;

    private CtClassBuilder(String className) {
        this.className = className;
        this.importStrings = new LinkedList<>();
        this.implementNames = new LinkedList<>();
        this.methods = new LinkedList<>();
    }

    public static CtClassBuilder builder(String className) {
        return new CtClassBuilder(className);
    }

    public void addImport(String importString) {
        importStrings.add(importString);
    }

    public void addImplement(String implementName) {
        implementNames.add(implementName);
    }

    public void addMethod(String methodString) {
        methods.add(methodString);
    }

    public CtClass build(ClassLoader classLoader) throws NotFoundException, CannotCompileException {
        ClassPool pool = ClassPool.getDefault();
        pool.appendClassPath(new LoaderClassPath(classLoader));

        CtClass ctClass = pool.makeClass(className);

        importStrings.forEach(pool::importPackage);

        for (String implementName : implementNames) {
            ctClass.addInterface(pool.get(implementName));
        }

        for (String method : methods) {
            ctClass.addMethod(CtNewMethod.make(method, ctClass));
        }

        return ctClass;
    }
}
