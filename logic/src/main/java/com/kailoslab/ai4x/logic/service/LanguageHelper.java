package com.kailoslab.ai4x.logic.service;

import com.kailoslab.ai4x.logic.code.ProgramLang;

import java.util.List;

public interface LanguageHelper {
    void initContext(String sessionId);
    ProgramLang getProgramLang();
    List<String> getInstalledPackages();
    boolean installPackage(String packageName);
}
