package com.kailoslab.ai4x.py4spring;

import com.kailoslab.ai4x.java2python.Java2PythonConverter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.context.ApplicationContext;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executor;

@Slf4j
public class Py4SpringPythonProxyRepository {

    public static final String PYTHON_PROXY_REPOSITORY = "py4SpringPythonProxyRepository";
    public static final String CREATE_PYTHON_PROXY_METHOD = "createPythonProxy";

    private final Map<String, PythonBeanInterceptor> interceptorMap;
    private final Map<String, Object> proxyMap;
    private final Py4SpringProperties properties;
    private final Executor executor;
    private final Java2PythonConverter pythonConverter;

    public Py4SpringPythonProxyRepository(ApplicationContext applicationContext, Py4SpringProperties properties, Executor executor) {
        this.properties = properties;
        this.executor = executor;
        this.interceptorMap = Collections.synchronizedMap(new HashMap<>());
        this.proxyMap = Collections.synchronizedMap(new HashMap<>());
        this.pythonConverter = new Java2PythonConverter();
        if(StringUtils.isNotEmpty(properties.getPythonDirectory())) {
            this.pythonConverter.setPythonSrcPath(properties.getPythonDirectory());
        }

        Optional appBeanOption = applicationContext.getBeansWithAnnotation(SpringBootApplication.class).values().stream().findFirst();
        if(appBeanOption.isPresent()) {
            String moduleName = appBeanOption.get().getClass().getPackageName();
            if (StringUtils.isNotEmpty(moduleName)) {
                if (StringUtils.contains(moduleName, ".")) {
                    moduleName = StringUtils.substringAfterLast(moduleName, ".");
                }

                this.pythonConverter.setPythonModuleName(moduleName);
            }
            // set default module name
        }
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
            if(properties.isConvert()) {
                createPythonCode(clazz);
            }

        }
        return bean;
    }

    public PythonBeanInterceptor getInterceptor(String className){
        return interceptorMap.get(className);
    }

    public boolean contains(String className) {
        return proxyMap.containsKey(className);
    }

    private void createPythonCode(Class clazz) {
        if(properties.isConvert()) {
            executor.execute(() -> pythonConverter.convert(clazz));
        }
    }
}
