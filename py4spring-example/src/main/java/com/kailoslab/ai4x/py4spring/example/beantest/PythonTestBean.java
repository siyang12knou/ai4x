package com.kailoslab.ai4x.py4spring.example.beantest;

import com.kailoslab.ai4x.py4spring.PythonProxy;

import java.util.Map;

@PythonProxy
public interface PythonTestBean {
    String name = "testBean";
    Map<String, Object> test(PythonTestArgs arg);

}
