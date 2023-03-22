package com.kailoslab.ai4x.java2python;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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
    public static final String PYTHON_INIT_ARGS_CODE = ", ${name}=${value}";

    public static final String PYTHON_VARIABLES_CODE = "\t\tself.${name} = ${name}\n";

    public static final String PYTHON_METHOD_CODE = """
            \tdef ${methodName}(self):
            \t\tpass
            
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
            pythonFileName = DEFAULT_PYTHON_FILE_NAME;
        } else {
            pythonFileName = StringUtils.replace(pythonFileName, ".", "_");
        }

        if(clazz.isInterface()) {
            convertInterface(clazz, pythonFileName);
        } else {
            convertClass(clazz, pythonFileName);
        }
    }

    private void convertInterface(Class clazz, String pythonFileName) {
        String className = clazz.getSimpleName();
        if(isExistClass(className, pythonFileName)) {
            Field[] fields = clazz.getDeclaredFields();
            StringBuilder initArgs = new StringBuilder();
            StringBuilder staticVariables = new StringBuilder();
            StringBuilder variables = new StringBuilder();
            for (Field field :
                    fields) {
                String name = field.getName();
                Object value = null;
                try {
                    value = field.get(null);
                } catch (IllegalAccessException ignored) {}
                if(value == null) {
                    Class<?> fieldClass = field.getType();
                    if(Number.class.isAssignableFrom(fieldClass)) {
                        value = DEFAULT_VALUE_NUMBER;
                    } else if (Dictionary.class.isAssignableFrom(fieldClass)) {
                        value = DEFAULT_VALUE_DICTIONARY;
                    } else if (Collection.class.isAssignableFrom(fieldClass)) {
                        value = DEFAULT_VALUE_COLLECTION;
                    } else {
                        value = NULL_VALUE;
                    }
                }

                String initArg = replace(PYTHON_INIT_ARGS_CODE, "name", name, "value", value);
                if(isPublicStaticFinal(field)) {
                    staticVariables.append("\t").append(initArg);
                } else {
                    initArgs.append(", ").append(initArg);
                }
                String variable = replace(PYTHON_VARIABLES_CODE, "name", name);
                variables.append("\t\t").append(variable);
            }
            System.out.println(PYTHON_CLASS_CODE);
        }
    }

    private void convertClass(Class clazz, String pythonFileName) {
        String className = clazz.getSimpleName();
        if(isExistClass(className, pythonFileName)) {
            Object obj = null;
            try {
                obj = clazz.getDeclaredConstructor().newInstance();
            } catch (Throwable e) {
                log.debug("Cannot find a default constructor.");
            }
        }
    }

    private boolean isExistClass(String className, String pythonFileName) {
        File file = new File(pythonFileName);

        try {
            Scanner scanner = new Scanner(file);

            int lineNum = 0;
            String classDeclare = replace(PYTHON_CLASS_DECLARE_CODE, "className", className);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                lineNum++;
                if(StringUtils.contains(line, classDeclare)) {
                    return false;
                }
            }
            return true;
        } catch(FileNotFoundException e) {
            return true;
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

            return StringSubstitutor.replace(PYTHON_CLASS_DECLARE_CODE, valueMap, "${", "}");
        }
    }

    private boolean isPublicStaticFinal(Field field) {
        int modifiers = field.getModifiers();
        return (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && Modifier
                .isFinal(modifiers));
    }
}
