package com.kailoslab.ai4x.java2python;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;

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

    public static final String PYTHON_INIT_ARGS_CODE = ", ${name}=${value}";

    public static final String PYTHON_VARIABLES_CODE = "\t\t${name} = ${value}\n";

    public static final String PYTHON_METHOD_CODE = """
            \tdef ${methodName}(self):
            \t\tpass
            
            """;

    public static final String PYTHON_CLASS_CODE = """
            class ${className}:
            
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
        Field[] fields = clazz.getDeclaredFields();
        StringBuilder variables = new StringBuilder();
        for (Field field :
                fields) {
            variables.append(", ").append(field.getName());
        }
        System.out.println(PYTHON_CLASS_CODE);
    }

    private void convertClass(Class clazz, String pythonModuleName) {
        Object obj = null;
        try {
            obj = clazz.getDeclaredConstructor().newInstance();
        } catch (Throwable e) {
            log.debug("Cannot find a default constructor.");
        }


    }
}
