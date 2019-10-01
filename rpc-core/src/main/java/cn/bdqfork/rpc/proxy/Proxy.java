package cn.bdqfork.rpc.proxy;

import cn.bdqfork.common.util.ReflectUtils;
import javassist.*;

import java.io.Serializable;
import java.lang.reflect.*;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author bdq
 * @since 2019/9/30
 */
public abstract class Proxy implements Serializable {
    private static final Map<String, Object> CACHE = new ConcurrentHashMap<>();
    private static final AtomicInteger PROXY_COUNTER = new AtomicInteger(0);

    public Proxy() {
    }

    public static Object newProxyInstance(ClassLoader classLoader, Class<?>[] interfaces, InvocationHandler handler) throws IllegalArgumentException, NotFoundException, CannotCompileException, IllegalAccessException, InstantiationException, NoSuchFieldException, NoSuchMethodException, InvocationTargetException {
        ClassGenerator generator = new ClassGenerator(classLoader);

        for (Class<?> interfaceClass : interfaces) {
            generator.addInterface(interfaceClass.getName());
        }

        String className = Proxy.class.getName() + PROXY_COUNTER.getAndIncrement();

        generator.setClassName(className);

        generator.setSuperClass(Proxy.class.getName());

        generator.addField("private java.lang.reflect.Method[] methods;");

        generator.addField("private " + InvocationHandler.class.getName() + " handler;");

        generator.addConstructor("public " + className.substring(className.lastIndexOf(".") + 1) + "(" + InvocationHandler.class.getName() + " handler){ $0.handler=$1;}");

        generator.addDefaultConstructor();

        Set<String> worked = new HashSet<>();
        List<Method> methods = new ArrayList<>();
        for (Class interfaceClass : interfaces) {
            for (Method method : interfaceClass.getMethods()) {
                if (worked.contains(ReflectUtils.getSign(method))) {
                    continue;
                }
                worked.add(ReflectUtils.getSign(method));
                methods.add(method);
            }
        }
        //TODO:返回值基础类型转换
        for (int i = 0; i < methods.size(); i++) {
            Method method = methods.get(i);
            String methodBodyBuilder = "Object result = $0.handler.invoke($0,$0.methods[" + i + "],$args); return result;";
            generator.addMethod(Modifier.PUBLIC, method.getReturnType(), method.getName(),
                    method.getParameterTypes(), method.getExceptionTypes(), methodBodyBuilder);
        }

        generator.addMethod("public Object newInstance(" + InvocationHandler.class.getName() + " handler){return new " + className + "($1);}");

        Class<?> clazz = generator.toClass();
        Proxy proxy = (Proxy) clazz.newInstance();
        proxy = (Proxy) proxy.newInstance(handler);
        Field handlerField = clazz.getDeclaredField("methods");
        handlerField.setAccessible(true);
        handlerField.set(proxy, methods.toArray(new Method[0]));
        return proxy;
    }

    public abstract Object newInstance(InvocationHandler handler);
}
