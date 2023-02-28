package com.kailoslab.ai4x.py4spring;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface PythonBean {
    @AliasFor(
            annotation = Component.class
    )
    String value() default "";
}
