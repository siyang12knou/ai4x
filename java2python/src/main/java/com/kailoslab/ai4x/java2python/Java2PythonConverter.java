package com.kailoslab.ai4x.java2python;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Slf4j
public class Java2PythonConverter {
    public static final String DEFAULT_PYTHON_FILE_NAME = "default";
    public static final String DEFAULT_VALUE_NUMBER = "0";
    public static final String DEFAULT_VALUE_DICTIONARY = "{}";
    public static final String DEFAULT_VALUE_COLLECTION = "[]";
    public static final String NULL_VALUE = "None";

    public static final String PYTHON_CLASS_DECLARE_CODE = "class ${className}:";
    public static final String PYTHON_INIT_ARGS_CODE = "${name}=${value}";

    public static final String PYTHON_VARIABLES_CODE = "\t\tself.${name} = ${name}\n";
    public static final String PYTHON_RETURN_CODE = "\t\treturn ${value}\n";
    public static final String PYTHON_PASS_CODE = "\t\tpass\n";
    public static final String PYTHON_SET_METHOD_CODE = """
            \tdef ${methodName}(self, ${name}):
            \t\tself.${name} = ${name}
            
            """;
    public static final String PYTHON_GET_METHOD_CODE = """
            \tdef ${methodName}(self):
            \t\treturn self.${name}
            
            """;

    public static final String PYTHON_METHOD_CODE = """
            \tdef ${methodName}(self${params}):
            ${body}
            
            """;

    public static final String PYTHON_CLASS_CODE =
            PYTHON_CLASS_DECLARE_CODE + "\n" + """
            
            ${staticVariables}
            \tdef __init__(self${initArgs}):
            ${variables}

            ${methods}
            \tclass Java:
            \t\timplements = ["${classFullName}"]
            """;

    private String pythonSrcPath = "./python";
    private String pythonModuleName = "spring";

    public void convert(Class clazz) {
        String pythonFileName = clazz.getPackageName();
        if(StringUtils.isEmpty(pythonFileName)) {
            pythonFileName = DEFAULT_PYTHON_FILE_NAME + ".py";
        } else {
            pythonFileName = StringUtils.replace(pythonFileName, ".", "_") + ".py";
        }

        Path pythonModulePath = Paths.get(pythonSrcPath, pythonModuleName);
        Path pythonFilePath = Paths.get(pythonSrcPath, pythonModuleName, pythonFileName);
        if(!isExistClass(clazz, pythonFilePath)) {
            String pythonCode = StringUtils.trimToNull(generatePythonCode(clazz));
            if(StringUtils.isNotEmpty(pythonCode)) {
                if(Files.notExists(pythonModulePath)) {
                    try {
                        Files.createDirectories(pythonModulePath);
                    } catch (IOException e) {
                        log.error("Cannot create a directory of module({}): {}", pythonModulePath, e);
                        return;
                    }
                }

                writeCode(pythonCode + "\n\n", pythonFilePath);
            }
        }
    }

    private String generatePythonCode(Class clazz) {
        String className = clazz.getSimpleName();
        Field[] fields = clazz.getDeclaredFields();
        StringBuilder initArgs = new StringBuilder();
        StringBuilder staticVariables = new StringBuilder();
        StringBuilder variables = new StringBuilder();
        StringBuilder methods = new StringBuilder();
        List<String> setGetMethodNames = new ArrayList<>();
        for (Field field :
                fields) {
            if(field.getType().isAnnotationPresent(PythonClass.class)) {
                convert(field.getType());
            }
            String name = field.getName();
            Object value = null;
            if(isPublicStaticFinal(field)) {
                try {
                    value = field.get(null);
                    if(String.class.isAssignableFrom(field.getType())) {
                        value = "'" + value + "'";
                    }
                } catch (Throwable ignored) {
                }
            }

            if(value == null) {
                value = getDefaultValue(field.getType());
            }

            String initArg = replace(PYTHON_INIT_ARGS_CODE, "name", name, "value", value);
            if(isPublicStaticFinal(field)) {
                staticVariables.append('\t').append(initArg).append('\n');
            } else {
                initArgs.append(", ").append(initArg);
                String variable = replace(PYTHON_VARIABLES_CODE, "name", name);
                variables.append(variable);
            }

            try {
                String setMethodName = "set" + StringUtils.capitalize(name);
                Method setMethod = clazz.getMethod(setMethodName, field.getType());
                if(Modifier.isPublic(setMethod.getModifiers())) {
                    methods.append(replace(PYTHON_SET_METHOD_CODE, "methodName", setMethodName, "name", name));
                    setGetMethodNames.add(setMethodName);
                }
            } catch (NoSuchMethodException ignored) {}

            try {
                String getMethodName = "get" + StringUtils.capitalize(name);
                Method getMethod = clazz.getMethod(getMethodName, field.getType());
                if(Modifier.isPublic(getMethod.getModifiers())) {
                    methods.append(replace(PYTHON_GET_METHOD_CODE, "methodName", getMethodName, "name", name));
                    setGetMethodNames.add(getMethodName);
                }
            } catch (NoSuchMethodException ignored) {}
        }

        Method[] methodList = clazz.getDeclaredMethods();
        for (Method method :
                methodList) {
            if(Modifier.isPublic(method.getModifiers()) && !setGetMethodNames.contains(method.getName())) {
                Parameter[] parameters = method.getParameters();
                StringBuilder initArgOfParameters = new StringBuilder();
                for (Parameter parameter:
                        parameters) {
                    if(parameter.getType().isAnnotationPresent(PythonClass.class)) {
                        convert(parameter.getType());
                    }

                    String name = parameter.getName();
                    Object value = getDefaultValue(parameter.getType());
                    String initArg = replace(PYTHON_INIT_ARGS_CODE, "name", name, "value", value);
                    initArgOfParameters.append(", ").append(initArg);
                }

                Class<?> returnType = method.getReturnType();
                if(returnType.isAnnotationPresent(PythonClass.class)) {
                    convert(returnType);
                }

                String body;
                if(Void.TYPE.equals(returnType)) {
                    body = PYTHON_PASS_CODE;
                } else {
                    body = replace(PYTHON_RETURN_CODE, "value", getDefaultValue(returnType));
                }

                methods.append(replace(PYTHON_METHOD_CODE,
                        "methodName", method.getName(),
                        "params", initArgOfParameters, "body", body));
            }
        }

        return replace(PYTHON_CLASS_CODE,
                "className", className,
                "staticVariables", staticVariables,
                "initArgs", initArgs,
                "variables", variables.isEmpty() ? PYTHON_PASS_CODE : variables,
                "methods", methods,
                "classFullName", clazz.getName()
        );
    }

    private void writeCode(String code, Path pythonFilePath) {
        try {
            if(Files.notExists(pythonFilePath)) {
                Files.createFile(pythonFilePath);
            }

            Files.write(pythonFilePath, code.getBytes(), StandardOpenOption.APPEND);
            log.info("Wrote a code to file({})", pythonFilePath);
        } catch (IOException e) {
            log.error("Cannot write a code to file({}): {}", pythonFilePath, e);
        }
    }

    private boolean isExistClass(Class<?> clazz, Path pythonFilePath) {
        return isExistClass(clazz.getSimpleName(), pythonFilePath);
    }

    private boolean isExistClass(String className, Path pythonFilePath) {
        try (Scanner scanner = new Scanner(pythonFilePath.toFile())){
            int lineNum = 0;
            String classDeclare = replace(PYTHON_CLASS_DECLARE_CODE, "className", className);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                lineNum++;
                if(StringUtils.contains(line, classDeclare)) {
                    return true;
                }
            }
            return false;
        } catch(FileNotFoundException e) {
            return false;
        }
    }

    private String replace(String template, Object... keyValue) {
        if(keyValue.length < 2 || keyValue.length % 2 != 0) {
            return template;
        } else {
            int i = 0;
            Map<String, String> valueMap = new HashMap<>(keyValue.length / 2);
            while (i < keyValue.length) {
                valueMap.put(keyValue[i].toString(), keyValue[++i].toString());
                i++;
            }

            return StringSubstitutor.replace(template, valueMap, "${", "}");
        }
    }

    private boolean isPublicStaticFinal(Field field) {
        int modifiers = field.getModifiers();
        return (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier
                .isFinal(modifiers));
    }

    private Object getDefaultValue(Class<?> clazz) {
        Object value;
        if(Number.class.isAssignableFrom(clazz)) {
            value = DEFAULT_VALUE_NUMBER;
        } else if (Dictionary.class.isAssignableFrom(clazz)) {
            value = DEFAULT_VALUE_DICTIONARY;
        } else if (Collection.class.isAssignableFrom(clazz)) {
            value = DEFAULT_VALUE_COLLECTION;
        } else {
            value = NULL_VALUE;
        }

        return value;
    }
}
