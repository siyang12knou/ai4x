package com.kailoslab.ai4x.py4spring;

import java.util.List;
import java.util.Map;

public interface IPy4SpringContext {
    void registerPythonContext(IPythonContext pythonContext);
    Object getBean(String qualifier);
    Object getBeanOfType(String className);
    Object getBean(String className, String qualifier);
    List<String> registerBean(IPythonBeanWrapper beanWrapper, List<String> classNames);
    List<String> registerBeanSync(IPythonBeanWrapper beanWrapper, List<String> classNames);
    void unregisterBean(List<String> classNames);
    Map<String, ?> getSpringSystemInfo();
}
