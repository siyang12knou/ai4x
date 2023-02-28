package com.kailoslab.ai4x.py4spring;

import java.lang.reflect.InvocationTargetException;

public class Py4Utils {
    public Object newInstance(String className, Object... args) throws Py4SpringException {
        try {
            Class<?> clazz = Class.forName(className);
            return clazz.getDeclaredConstructor().newInstance(args);
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new Py4SpringException("Cannot find a class " + className, e);
        }
    }

    public Object convertToReturnType(Object orgInstance) {
        return orgInstance;
    }

    public Object convertToReturnType(Object orgInstance, String className, String methodName) {
        return orgInstance;
    }

    public Object convertToInstance(Object orgInstance, String targetClassName) {
        return orgInstance;
    }
}
