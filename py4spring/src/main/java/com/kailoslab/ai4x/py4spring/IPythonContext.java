package com.kailoslab.ai4x.py4spring;

import java.util.List;
import java.util.Map;

public interface IPythonContext {
    String getQualifier();
    void setConnected(Boolean connected);
    Map<String, String> getPythonSystemInfo();
    List<String> getPackagesInfo();
}
