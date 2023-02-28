package com.kailoslab.ai4x.py4spring;

import java.util.List;

public interface IPythonBeanWrapper {
    List<String> getClassNames();
    Object getBean();
}
