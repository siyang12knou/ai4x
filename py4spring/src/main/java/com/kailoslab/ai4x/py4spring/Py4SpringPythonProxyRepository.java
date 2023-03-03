package com.kailoslab.ai4x.py4spring;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.proxy.Enhancer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class Py4SpringPythonProxyRepository {

    public static final String PYTHON_PROXY_REPOSITORY = "py4SpringPythonProxyRepository";
    public static final String CREATE_PYTHON_PROXY_METHOD = "createPythonProxy";

    private final Map<String, PythonBeanInterceptor> interceptorMap;
    private final Map<String, Object> proxyMap;

    public Py4SpringPythonProxyRepository() {
        this.interceptorMap = Collections.synchronizedMap(new HashMap<>());
        this.proxyMap = Collections.synchronizedMap(new HashMap<>());
    }

    public Object createPythonProxy(Class clazz) {
        String className = clazz.getName();
        Object bean = proxyMap.get(className);
        if(bean == null) {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(clazz);
            PythonBeanInterceptor interceptor = new PythonBeanInterceptor(className);
            enhancer.setCallback(interceptor);
            bean = enhancer.create();
            interceptorMap.put(className, interceptor);
            proxyMap.put(className, bean);

        }
        return bean;
    }

    public PythonBeanInterceptor getInterceptor(String className){
        return interceptorMap.get(className);
    }

    public boolean contains(String className) {
        return proxyMap.containsKey(className);
    }

}
