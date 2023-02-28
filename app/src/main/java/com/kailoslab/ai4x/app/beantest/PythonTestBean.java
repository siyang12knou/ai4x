package com.kailoslab.ai4x.app.beantest;

import com.kailoslab.ai4x.py4spring.PythonBean;

import java.util.Map;

@PythonBean
public interface PythonTestBean {

    Map<String, Object> test(PythonTestArgs arg);

}
