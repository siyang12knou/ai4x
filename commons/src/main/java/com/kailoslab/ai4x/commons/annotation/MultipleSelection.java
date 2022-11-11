package com.kailoslab.ai4x.commons.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MultipleSelection {
    boolean value() default false;
}
