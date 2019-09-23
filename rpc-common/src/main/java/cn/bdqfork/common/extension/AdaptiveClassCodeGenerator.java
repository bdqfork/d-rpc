package cn.bdqfork.common.extension;

import cn.bdqfork.common.URL;

import java.lang.reflect.Method;

/**
 * @author bdq
 * @since 2019/9/22
 */
public class AdaptiveClassCodeGenerator {
    private static final String PACKAGE_FORMATER = "package %s;\n";
    private static final String IMPORT_FORMATER = "import %s;\n";
    private static final String CLASS_FORMATER = "public class %s$Adaptive implements %s {\n%s}\n";
    private static final String METHOD_FORMATER = "public %s %s(%s) %s {\n%s}\n";
    private static final String ARGUMENTS_FORMATER = "%s arg%d";
    private static final String THROW_FORMATER = "throws %s";
    private static final String URL_FORMATER = "%s url = %s;\n";
    private static final String CODE_KEYS_FORMATER = "String[] keys = new String[]{%s};\n";
    private static final String EXTENSION_NAME_FORMATER = "if (extensionName == null) { extensionName = %s;\n}\n";
    private static final String EXTENSION_FORMATER = "%s extension = (%s)%s.getExtensionLoader(%s.class).getExtension(extensionName);\n";
    private static final String ILLEGAL_FORMATER = "if (extension == null) {\nthrow new IllegalStateException(\"Fail to get extension for class %s use keys (%s) !\");\n}";
    private static final String RETURN_FORMATER = "%s extension.%s(%s);\n";
    private static final String UNSUPPORT_FORMATER = "throw new UnsupportedOperationException(\"Method %s is not annotated by @Adaptive !\");";
    private Class<?> type;
    private String defaultName;

    public AdaptiveClassCodeGenerator(Class<?> type, String defaultName) {
        this.type = type;
        this.defaultName = defaultName;
    }

    public String generate() {
        boolean hasAdaptive = false;
        Method[] methods = type.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getAnnotation(Adaptive.class) != null) {
                hasAdaptive = true;
                break;
            }
        }

        if (!hasAdaptive) {
            throw new IllegalStateException("No @Adaptive method in class " + type.getName() + "!");
        }

        StringBuilder code = new StringBuilder();
        String packageName = String.format(PACKAGE_FORMATER, type.getPackage().getName());
        code.append(packageName);
        String importName = String.format(IMPORT_FORMATER, ExtensionLoader.class.getCanonicalName());
        code.append(importName);

        StringBuilder methodsCodeBuilder = new StringBuilder();
        for (Method method : methods) {
            String methodName = method.getName();
            String returnTypeName = method.getReturnType().getTypeName();
            Class<?>[] parameters = method.getParameterTypes();
            String argsCode = getArgsCode(parameters);

            String exceptionCode = generateExceptionCode(method);

            String bodyCode;
            Adaptive adaptive = method.getAnnotation(Adaptive.class);
            if (adaptive != null) {
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
                        try {
                            parameters[i].getMethod("getUrl");
                            index = i;
                            break;
                        } catch (NoSuchMethodException ignored) {
                        }
                    }
                }
                if (index == -1) {
                    throw new IllegalStateException("Can't get URL from method " +
                            method.getName() + " for class " + type.getName() + "!");
                }
                String urlCode;
                Class<?> parameter = parameters[index];
                if (parameter == URL.class) {
                    urlCode = String.format(URL_FORMATER, URL.class.getCanonicalName(), "arg" + index);
                } else {
                    urlCode = String.format(URL_FORMATER, URL.class.getCanonicalName(), "arg" + index + ".getUrl()");
                }

                StringBuilder keysCodeBuilder = new StringBuilder();
                String[] value = adaptive.value();
                for (String key : value) {
                    keysCodeBuilder.append("\"")
                            .append(key)
                            .append("\"")
                            .append(",");
                }
                removeLastChar(keysCodeBuilder);
                String keys = keysCodeBuilder.toString();
                String keyCode = String.format(CODE_KEYS_FORMATER, keys);
                StringBuilder extensionNameCodeBuilder = new StringBuilder();
                extensionNameCodeBuilder.append("String extensionName = null;\n")
                        .append("for(int i=0; i < keys.length; i++) {\n")
                        .append("extensionName = (String)url.getParameter(keys[i]);\n")
                        .append("if (extensionName != null) {\n break;\n}\n}\n");
                String defaultExtensionNameCode = String.format(EXTENSION_NAME_FORMATER, defaultName == null ? null : "\"" + defaultName + "\"");
                extensionNameCodeBuilder.append(defaultExtensionNameCode);

                String extensionNameCode = extensionNameCodeBuilder.toString();

                String extensionCode = String.format(EXTENSION_FORMATER, type.getCanonicalName(), type.getCanonicalName(), ExtensionLoader.class.getSimpleName(), type.getCanonicalName());

                String illegalCode = String.format(ILLEGAL_FORMATER, type.getCanonicalName(), "");

                StringBuilder argsBuilder = new StringBuilder();
                for (int i = 0; i < parameters.length; i++) {
                    argsBuilder.append("arg").append(i).append(",");
                }
                removeLastChar(argsBuilder);

                String returnCode;
                if (!method.getReturnType().equals(void.class)) {
                    returnCode = String.format(RETURN_FORMATER, "return", methodName, argsBuilder.toString());
                } else {
                    returnCode = String.format(RETURN_FORMATER, "", methodName, argsBuilder.toString());
                }

                bodyCode = urlCode +
                        keyCode +
                        extensionNameCode +
                        extensionCode +
                        illegalCode +
                        returnCode;
            } else {
                bodyCode = String.format(UNSUPPORT_FORMATER, method.getName());
            }
            String methodCode = String.format(METHOD_FORMATER, returnTypeName, methodName, argsCode, exceptionCode, bodyCode);
            methodsCodeBuilder.append(methodCode);
        }
        String classCode = String.format(CLASS_FORMATER, type.getSimpleName(), type.getCanonicalName(), methodsCodeBuilder.toString());
        code.append(classCode);
        return code.toString();
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
        removeLastChar(exceptionCodeBuilder);
        return String.format(THROW_FORMATER, exceptionCodeBuilder.toString());
    }

    private String getArgsCode(Class<?>[] parameters) {
        StringBuilder argsCodeBuilder = new StringBuilder();
        for (int i = 0; i < parameters.length; i++) {
            argsCodeBuilder.append(String.format(ARGUMENTS_FORMATER, parameters[i].getCanonicalName(), i))
                    .append(",");
        }
        removeLastChar(argsCodeBuilder);
        return argsCodeBuilder.toString();
    }

    private void removeLastChar(StringBuilder argsCodeBuilder) {
        if (argsCodeBuilder.length() > 0) {
            argsCodeBuilder.deleteCharAt(argsCodeBuilder.length() - 1);
        }
    }
}
