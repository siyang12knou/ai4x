package com.kailoslab.ai4x.commons.annotation;

import java.lang.annotation.*;

@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CodeGroup {
    String value() default "";
}
