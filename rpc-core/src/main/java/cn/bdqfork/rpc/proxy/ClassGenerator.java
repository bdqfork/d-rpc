package cn.bdqfork.rpc.proxy;

import cn.bdqfork.common.util.StringUtils;
import javassist.*;

import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;
import java.util.LinkedList;
import java.util.List;

/**
 * @author bdq
 * @since 2019/10/1
 */
public class ClassGenerator {
    private ClassPool classPool;
    private String className;
    private String superClass;
    private boolean addDefaultConstructor;
    private List<String> interfaces;
    private List<String> constructors;
    private List<String> fields;
    private List<String> methods;

    public ClassGenerator() {
        this(null);
    }

    public ClassGenerator(ClassLoader classLoader) {
        if (classLoader == null) {
            classPool = ClassPool.getDefault();
        } else {
            classPool = new ClassPool(true);
            classPool.appendClassPath(new LoaderClassPath(classLoader));
        }
    }

    private static String modifier(int modfier) {
        StringBuilder modifier = new StringBuilder();
        if (java.lang.reflect.Modifier.isPublic(modfier)) {
            modifier.append("public");
        }
        if (java.lang.reflect.Modifier.isProtected(modfier)) {
            modifier.append("protected");
        }
        if (java.lang.reflect.Modifier.isPrivate(modfier)) {
            modifier.append("private");
        }

        if (java.lang.reflect.Modifier.isStatic(modfier)) {
            modifier.append(" static");
        }
        if (Modifier.isVolatile(modfier)) {
            modifier.append(" volatile");
        }

        return modifier.toString();
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setSuperClass(String superClass) {
        this.superClass = superClass;
    }

    public void addInterface(String interfaceName) {
        if (interfaces == null) {
            interfaces = new LinkedList<>();
        }
        interfaces.add(interfaceName);
    }

    public void addConstructor(String constructor) {
        if (constructors == null) {
            constructors = new LinkedList<>();
        }
        constructors.add(constructor);
    }

    public void addDefaultConstructor() {
        addDefaultConstructor = true;
    }

    public void addField(String field) {
        if (fields == null) {
            fields = new LinkedList<>();
        }
        fields.add(field);
    }

    public void addMethod(String method) {
        if (methods == null) {
            methods = new LinkedList<>();
        }
        methods.add(method);
    }

    public void addMethod(int modifier, Class<?> returnType, String methodName, Class<?>[] parameterTypes,
                          Class<?>[] exceptionTypes, String body) {
        StringBuilder methodBuilder = new StringBuilder();
        methodBuilder.append(modifier(modifier))
                .append(" ")
                .append(returnType.getName());
        methodBuilder.append(" ").append(methodName).append("(");

        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            methodBuilder.append(parameterType.getName())
                    .append(" arg")
                    .append(i)
                    .append(",");
        }
        StringUtils.removeLastChar(methodBuilder);
        methodBuilder.append(")");

        if (exceptionTypes != null && exceptionTypes.length > 0) {
            methodBuilder.append("throws ");

            for (Class<?> exceptionType : exceptionTypes) {
                methodBuilder.append(exceptionType.getName())
                        .append(",");
            }
            StringUtils.removeLastChar(methodBuilder);
        }

        methodBuilder.append("{").append(body).append("}");

        addMethod(methodBuilder.toString());
    }

    public Class<?> toClass() {
        return toClass(ClassGenerator.class.getClassLoader(), ClassGenerator.class.getProtectionDomain());
    }

    public Class<?> toClass(ClassLoader classLoader, ProtectionDomain protectionDomain) {
        //TODO:参数空校验
        try {
            CtClass ctClass = classPool.makeClass(className);

            ctClass.setSuperclass(classPool.get(superClass));

            for (String interfaceName : interfaces) {
                ctClass.addInterface(classPool.get(interfaceName));
            }

            if (addDefaultConstructor) {
                ctClass.addConstructor(CtNewConstructor.defaultConstructor(ctClass));
            }

            for (String field : fields) {
                ctClass.addField(CtField.make(field, ctClass));
            }

            for (String constructor : constructors) {
                ctClass.addConstructor(CtNewConstructor.make(constructor, ctClass));
            }

            for (String method : methods) {
                ctClass.addMethod(CtNewMethod.make(method, ctClass));
            }

            return ctClass.toClass(classLoader, protectionDomain);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
