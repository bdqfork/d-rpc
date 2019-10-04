package cn.bdqfork.rpc.proxy;

import cn.bdqfork.common.util.StringUtils;
import javassist.*;

import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author bdq
 * @since 2019/10/1
 */
public class ClassGenerator {
    private static final Map<ClassLoader, ClassPool> POOL_CACHE = new ConcurrentHashMap<>();
    private static final String INIT_FLAG = "<init>";
    private ClassPool classPool;
    private String className;
    private String simpleName;
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
            classPool = POOL_CACHE.get(classLoader);
            if (classPool == null) {
                classPool = new ClassPool(true);
                classPool.appendClassPath(new LoaderClassPath(classLoader));
                POOL_CACHE.putIfAbsent(classLoader, classPool);
            }
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

    public ClassGenerator setClassName(String className) {
        this.className = className;
        this.simpleName = className.substring(className.lastIndexOf(".") + 1);
        return this;
    }

    public ClassGenerator setSuperClass(String superClass) {
        this.superClass = superClass;
        return this;
    }

    public ClassGenerator addInterface(String interfaceName) {
        if (interfaces == null) {
            interfaces = new LinkedList<>();
        }
        interfaces.add(interfaceName);
        return this;
    }

    public ClassGenerator addConstructor(String constructor) {
        if (constructors == null) {
            constructors = new LinkedList<>();
        }
        constructors.add(constructor);
        return this;
    }

    public ClassGenerator addConstructor(int modifier, Class<?>[] parameters, String body) {
        return addConstructor(modifier, parameters, null, body);
    }

    public ClassGenerator addConstructor(int modifier, Class<?>[] parameters, Class<?>[] exceptions, String body) {
        StringBuilder codeBuilder = new StringBuilder();
        codeBuilder.append(modifier(modifier))
                .append(" ")
                .append(INIT_FLAG)
                .append("(");
        for (int i = 0; i < parameters.length; i++) {
            if (i > 0) {
                codeBuilder.append(",");
            }
            Class<?> parameter = parameters[i];
            codeBuilder.append(parameter.getCanonicalName())
                    .append(" ")
                    .append("arg")
                    .append(i);
        }
        codeBuilder.append(")");
        if (exceptions != null && exceptions.length > 0) {
            codeBuilder.append("throws ");
            for (int i = 0; i < exceptions.length; i++) {
                if (i > 0) {
                    codeBuilder.append(",");
                }
                Class<?> exceptionClass = exceptions[i];
                codeBuilder.append(exceptionClass.getCanonicalName());
            }
        }
        codeBuilder.append("{")
                .append(body)
                .append("}");
        return addConstructor(codeBuilder.toString());
    }

    public ClassGenerator addDefaultConstructor() {
        addDefaultConstructor = true;
        return this;
    }

    public ClassGenerator addField(String field) {
        if (fields == null) {
            fields = new LinkedList<>();
        }
        fields.add(field);
        return this;
    }

    public ClassGenerator addMethod(String method) {
        if (methods == null) {
            methods = new LinkedList<>();
        }
        methods.add(method);
        return this;
    }

    public ClassGenerator addMethod(int modifier, Class<?> returnType, String methodName, Class<?>[] parameterTypes,
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
        return this;
    }

    public Class<?> toClass() {
        return toClass(ClassGenerator.class.getClassLoader(), ClassGenerator.class.getProtectionDomain());
    }

    public Class<?> toClass(ClassLoader classLoader, ProtectionDomain protectionDomain) {
        try {
            CtClass ctClass = classPool.makeClass(className);

            if (superClass != null) {
                ctClass.setSuperclass(classPool.get(superClass));
            }

            if (interfaces != null) {
                for (String interfaceName : interfaces) {
                    ctClass.addInterface(classPool.get(interfaceName));
                }
            }

            if (addDefaultConstructor) {
                ctClass.addConstructor(CtNewConstructor.defaultConstructor(ctClass));
            }

            if (fields != null) {
                for (String field : fields) {
                    ctClass.addField(CtField.make(field, ctClass));
                }
            }

            if (constructors != null) {
                for (String constructor : constructors) {
                    if (constructor.contains(INIT_FLAG)) {
                        constructor = constructor.replace(INIT_FLAG, simpleName);
                    }
                    ctClass.addConstructor(CtNewConstructor.make(constructor, ctClass));
                }
            }

            if (methods != null) {
                for (String method : methods) {
                    ctClass.addMethod(CtNewMethod.make(method, ctClass));
                }
            }

            return ctClass.toClass(classLoader, protectionDomain);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}
