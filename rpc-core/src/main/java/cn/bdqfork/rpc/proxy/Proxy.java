package cn.bdqfork.rpc.proxy;

import cn.bdqfork.common.util.ReflectUtils;
import javassist.CannotCompileException;
import javassist.NotFoundException;

import java.io.Serializable;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * @author bdq
 * @since 2019/9/30
 */
public abstract class Proxy implements Serializable {
    private static final Map<String, Object> CACHE = Collections.synchronizedMap(new WeakHashMap<>());
    private static final AtomicLong PROXY_COUNTER = new AtomicLong(0);

    public Proxy() {
    }

    public static Object newProxyInstance(ClassLoader classLoader, Class<?>[] interfaces, InvocationHandler handler) throws IllegalArgumentException {
        String key = getKey(interfaces);
        if (CACHE.containsKey(key)) {
            return CACHE.get(key);
        }
        ClassGenerator generator = new ClassGenerator(classLoader);

        for (Class<?> interfaceClass : interfaces) {
            generator.addInterface(interfaceClass.getName());
        }

        String className = Proxy.class.getName() + PROXY_COUNTER.getAndIncrement();

        generator.setClassName(className).setSuperClass(Proxy.class.getName());

        generator.addField("private java.lang.reflect.Method[] methods;");
        generator.addField("private " + InvocationHandler.class.getName() + " handler;");

        generator.addConstructor(Modifier.PUBLIC, new Class[]{InvocationHandler.class}, "$0.handler=$1;");
        generator.addDefaultConstructor();

        Set<String> worked = new HashSet<>();
        List<Method> methods = new ArrayList<>();
        for (Class interfaceClass : interfaces) {
            for (Method method : interfaceClass.getMethods()) {
                if (worked.contains(ReflectUtils.getSignature(method))) {
                    continue;
                }
                worked.add(ReflectUtils.getSignature(method));
                methods.add(method);
            }
        }

        for (int i = 0; i < methods.size(); i++) {
            Method method = methods.get(i);
            StringBuilder codeBuilder = new StringBuilder();
            codeBuilder.append("Object result = $0.handler.invoke($0,$0.methods[").append(i).append("],$args);");
            Class<?> returnType = method.getReturnType();
            if (!Void.TYPE.equals(returnType)) {
                codeBuilder.append("return ").append(castResult("result", returnType));
            }
            generator.addMethod(Modifier.PUBLIC, returnType, method.getName(),
                    method.getParameterTypes(), method.getExceptionTypes(), codeBuilder.toString());
        }

        generator.addMethod("public Object newInstance(" + InvocationHandler.class.getName() + " handler){return new " + className + "($1);}");

        try {
            Class<?> clazz = generator.toClass();
            Proxy proxy = (Proxy) clazz.newInstance();
            proxy = (Proxy) proxy.newInstance(handler);
            Field handlerField = clazz.getDeclaredField("methods");
            handlerField.setAccessible(true);
            handlerField.set(proxy, methods.toArray(new Method[0]));
            CACHE.putIfAbsent(key, proxy);
            return proxy;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e.getCause());
        }

    }

    private static String getKey(Class<?>[] interfaces) {
        return Arrays.stream(interfaces)
                .map(Class::getName)
                .collect(Collectors.joining());
    }

    private static String castResult(String resultName, Class<?> returnType) {
        if (returnType.isPrimitive()) {
            if (Byte.TYPE == returnType) {
                return resultName + "==null? (byte)0:((Byte)" + resultName + ").byteValue();";
            }
            if (Short.TYPE == returnType) {
                return resultName + "==null? (short)0:((Short)" + resultName + ").shortValue();";
            }
            if (Integer.TYPE == returnType) {
                return resultName + "==null? (int)0:((Integer)" + resultName + ").intValue();";
            }
            if (Long.TYPE == returnType) {
                return resultName + "==null? (long)0:((Long)" + resultName + ").longValue();";
            }
            if (Float.TYPE == returnType) {
                return resultName + "==null? (float)0:((Float)" + resultName + ").floatValue();";
            }
            if (Double.TYPE == returnType) {
                return resultName + "==null? (double)0:((Double)" + resultName + ").doubleValue();";
            }
            if (Character.TYPE == returnType) {
                return resultName + "==null? (char)0:((Character)" + resultName + ").charValue();";
            }
            if (Boolean.TYPE == returnType) {
                return resultName + "==null? false:((Boolean)" + resultName + ").booleanValue();";
            }
            throw new RuntimeException("Unknow primitive " + returnType.getCanonicalName() + " !");
        }
        return "(" + returnType.getCanonicalName() + ")" + resultName + ";";
    }

    public abstract Object newInstance(InvocationHandler handler);
}
