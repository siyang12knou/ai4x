package com.kailoslab.ai4x.py4spring;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

@RequiredArgsConstructor
@Slf4j
@Getter
public class PythonBeanInterceptor implements MethodInterceptor{

    private final String className;
    private Object target;
    private boolean init;

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        if(target != null) {
            try {
                log.info("Call {}#{}", className, method.getName());
                Object result = proxy.invoke(target, args);
                log.info("Result of {}#{} is {}.", className, method.getName(), result);
                return result;
            } catch (Exception e) {
                log.error("Fire a exception when call {}#{}: {}", className, method.getName(), e);
                throw e;
            }
        } else if(method.getName().contains("toString")){
            return obj.toString();
        } else {
            String exceptionMessage = "No beans have been allocated yet: " + className;
            log.error(exceptionMessage);
            throw new IllegalStateException(exceptionMessage);
        }
    }

    public void setTarget(Object target) {
        this.target = target;
        this.init = true;
    }
}
