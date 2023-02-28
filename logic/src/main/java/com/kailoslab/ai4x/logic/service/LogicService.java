package com.kailoslab.ai4x.logic.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class LogicService {

    private Map<String, ILogic> logicMap = Collections.synchronizedMap(new HashMap<>());

    public void registerLogic(ILogic logic) {

    }

    public ILogic getLogic(String name) {
        return null;
    }
}
