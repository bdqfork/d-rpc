package cn.bdqfork.common.extension;

import cn.bdqfork.common.URL;
import cn.bdqfork.common.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 生成Adaptive的wrapper类
 *
 * @author bdq
 * @since 2019/9/22
 */
public class AdaptiveClassCodeGenerator {
    private static final Logger log = LoggerFactory.getLogger(AdaptiveClassCodeGenerator.class);

    private static final String PACKAGE_CODE_FORMATER = "package %s;\n";
    private static final String IMPORT_CODE_FORMATER = "import %s;\n";
    private static final String CLASS_DECLARE_CODE_FORMATER = "public class %s$Adaptive implements %s {\n%s}\n";
    private static final String METHOD_CODE_FORMATER = "public %s %s(%s) %s {\n%s}\n";
    private static final String ARGUMENT_CODE_FORMATER = "%s arg%d";
    private static final String THROW_CODE_FORMATER = "throws %s";
    private static final String URL_CODE_FORMATER = "%s url = %s;\n";
    private static final String KEYS_CODE_FORMATER = "String[] keys = new String[]{%s};\n";
    private static final String NAME_CODE_FORMATER = "if (extensionName == null){ extensionName = %s;\n}\n";
    private static final String EXTENSION_CODE_FORMATER = "%s extension = (%s)%s.getExtensionLoader(%s.class).getExtension(extensionName);\n";
    private static final String ILLEGAL_CODE_FORMATER = "if (extension == null) {\nthrow new IllegalStateException(\"Fail to get extension for class %s use keys (%s) !\");\n}";
    private static final String INVOKE_CODE_FORMATER = "%s extension.%s(%s);\n";
    private static final String UNSUPPORT_CODE_FORMATER = "throw new UnsupportedOperationException(\"Method %s is not annotated by @Adaptive !\");";

    private Class<?> type;
    private String defaultName;

    public AdaptiveClassCodeGenerator(Class<?> type, String defaultName) {
        this.type = type;
        this.defaultName = defaultName;
    }

    public String generate() {
        if (!hasAdaptive()) {
            throw new IllegalStateException("No @Adaptive method in class " + type.getName() + "!");
        }

        StringBuilder code = new StringBuilder();

        String packageName = getPackageName();
        code.append(packageName);

        String importName = getImportName();
        code.append(importName);

        String classCode = getClassDeclareCode();
        code.append(classCode);

        return code.toString();
    }

    private boolean hasAdaptive() {
        boolean hasAdaptive = false;
        Method[] methods = type.getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(Adaptive.class)) {
                hasAdaptive = true;
                break;
            }
        }
        return hasAdaptive;
    }

    private String getPackageName() {
        return String.format(PACKAGE_CODE_FORMATER, type.getPackage().getName());
    }

    private String getImportName() {
        return String.format(IMPORT_CODE_FORMATER, ExtensionLoader.class.getCanonicalName());
    }

    private String getClassDeclareCode() {
        return String.format(CLASS_DECLARE_CODE_FORMATER, type.getSimpleName(), type.getCanonicalName(), getMethodCodes());
    }


    private String getMethodCodes() {
        return Arrays.stream(type.getDeclaredMethods())
                .map(this::getMethodCode)
                .collect(Collectors.joining());
    }

    private String getMethodCode(Method method) {
        String methodName = method.getName();

        String bodyCode = getMethodBodyCode(method);

        String returnTypeName = method.getReturnType().getTypeName();

        String argsCode = getArgsCode(method);

        String exceptionCode = generateExceptionCode(method);

        return String.format(METHOD_CODE_FORMATER, returnTypeName, methodName, argsCode, exceptionCode, bodyCode);
    }

    private String getMethodBodyCode(Method method) {
        if (method.isAnnotationPresent(Adaptive.class)) {
            return getAdaptiveMethodBodyCode(method);
        }
        return getUnsupportMethodBodyCode(method);
    }

    private String getAdaptiveMethodBodyCode(Method method) {
        String urlCode = getUrlCode(method);

        String keyCode = getKeyCode(method);

        String extensionNameCode = getNameCode();

        String extensionCode = getExtensionCode();

        String illegalCode = getIllegalCode(method);

        String invokeCode = getInvokeCode(method);

        return urlCode + keyCode + extensionNameCode + extensionCode + illegalCode + invokeCode;
    }

    private String getUrlCode(Method method) {
        int index = getUrlIndex(method);
        Class<?> parameter = method.getParameterTypes()[index];
        if (parameter == URL.class) {
            return String.format(URL_CODE_FORMATER, URL.class.getCanonicalName(), "arg" + index);
        }
        return String.format(URL_CODE_FORMATER, URL.class.getCanonicalName(), "arg" + index + ".getUrl()");
    }

    private int getUrlIndex(Method method) {
        Class<?>[] parameters = method.getParameterTypes();
        int index = -1;
        for (int i = 0; i < parameters.length; i++) {
            Class<?> parameter = parameters[i];
            if (parameter == URL.class) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            for (int i = 0; i < parameters.length; i++) {
                Class<?> parameter = parameters[i];
                if (hasUrlGetter(parameter)) {
                    index = i;
                    break;
                }
                log.debug("Can't find URL getter in {}.{}'s paramter {} !", type.getCanonicalName(),
                        method.getName(), parameter.getTypeName());
            }
        }
        if (index == -1) {
            throw new IllegalStateException("Can't get URL from method " +
                    method.getName() + " for class " + type.getName() + "!");
        }
        return index;
    }

    private boolean hasUrlGetter(Class<?> parameter) {
        try {
            parameter.getMethod("getUrl");
            return true;
        } catch (NoSuchMethodException ignored) {
            return false;
        }
    }

    private String getKeyCode(Method method) {
        Adaptive adaptive = method.getAnnotation(Adaptive.class);
        if (adaptive.value().length == 0) {
            return "String[] keys = null;\n";
        }
        StringBuilder keysCodeBuilder = new StringBuilder();
        for (String key : adaptive.value()) {
            keysCodeBuilder.append("\"")
                    .append(key)
                    .append("\"")
                    .append(",");
        }
        StringUtils.removeLastChar(keysCodeBuilder);
        return String.format(KEYS_CODE_FORMATER, keysCodeBuilder.toString());
    }

    private String getNameCode() {
        StringBuilder extensionNameCodeBuilder = new StringBuilder();
        extensionNameCodeBuilder.append("String extensionName = null;\n")
                .append("if (keys != null){\n")
                .append("for(int i=0; i < keys.length; i++) {\n")
                .append("extensionName = (String)url.getParameter(keys[i]);\n")
                .append("if (extensionName != null) {\n break;\n}\n}\n}")
                .append("else {\nextensionName=(String)url.getProtocol();\n}\n");
        String defaultExtensionNameCode = String.format(NAME_CODE_FORMATER, defaultName == null ? null : "\"" + defaultName + "\"");
        extensionNameCodeBuilder.append(defaultExtensionNameCode);

        return extensionNameCodeBuilder.toString();
    }

    private String getExtensionCode() {
        return String.format(EXTENSION_CODE_FORMATER, type.getCanonicalName(), type.getCanonicalName(),
                ExtensionLoader.class.getSimpleName(), type.getCanonicalName());
    }

    private String getIllegalCode(Method method) {
        StringBuilder keysCodeBuilder = new StringBuilder();
        Adaptive adaptive = method.getAnnotation(Adaptive.class);
        for (String key : adaptive.value()) {
            keysCodeBuilder.append(key)
                    .append(",");
        }
        StringUtils.removeLastChar(keysCodeBuilder);
        return String.format(ILLEGAL_CODE_FORMATER, type.getCanonicalName(), keysCodeBuilder.toString());
    }

    private String getInvokeCode(Method method) {
        String methodName = method.getName();
        String argNames = getArgNames(method.getParameterTypes());
        if (method.getReturnType().equals(void.class)) {
            return String.format(INVOKE_CODE_FORMATER, "", methodName, argNames);
        }
        return String.format(INVOKE_CODE_FORMATER, "return", methodName, argNames);
    }

    private String getArgNames(Class<?>[] parameters) {
        StringBuilder argNamesBuilder = new StringBuilder();
        for (int i = 0; i < parameters.length; i++) {
            argNamesBuilder.append("arg").append(i).append(",");
        }
        StringUtils.removeLastChar(argNamesBuilder);
        return argNamesBuilder.toString();
    }

    private String getUnsupportMethodBodyCode(Method method) {
        return String.format(UNSUPPORT_CODE_FORMATER, method.getName());
    }

    private String getArgsCode(Method method) {
        StringBuilder argsCodeBuilder = new StringBuilder();
        Class<?>[] parameters = method.getParameterTypes();
        for (int i = 0; i < parameters.length; i++) {
            argsCodeBuilder.append(String.format(ARGUMENT_CODE_FORMATER, parameters[i].getCanonicalName(), i))
                    .append(",");
        }
        StringUtils.removeLastChar(argsCodeBuilder);
        return argsCodeBuilder.toString();
    }

    private String generateExceptionCode(Method method) {
        Class<?>[] exceptions = method.getExceptionTypes();
        if (exceptions.length == 0) {
            return "";
        }
        StringBuilder exceptionCodeBuilder = new StringBuilder();
        for (Class<?> exception : exceptions) {
            exceptionCodeBuilder.append(exception.getCanonicalName()).append(",");
        }
        StringUtils.removeLastChar(exceptionCodeBuilder);
        return String.format(THROW_CODE_FORMATER, exceptionCodeBuilder.toString());
    }

}
