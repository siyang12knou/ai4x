package com.kailoslab.ai4x.logic.service;

public interface ILogic {
    String getName();
    Object getInputMetaInfo();
    Object getOutputMetaInfo();
    Object execute(Object... args);
}
