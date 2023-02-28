package com.kailoslab.ai4x.py4spring.controller;

public interface IPythonRestFunction {
    String getPath();
    String getMethodName();
    Object call(Object httpInfo);
}
