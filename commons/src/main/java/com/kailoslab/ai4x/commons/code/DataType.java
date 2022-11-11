package com.kailoslab.ai4x.commons.code;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum DataType {
    String(FormFieldType.TEXTFIELD, String.class),
    Number(FormFieldType.TEXTFIELD, Number.class),
    Boolean(FormFieldType.CHECK, Boolean.class),
    Object(FormFieldType.TEXTFIELD),
    Array(FormFieldType.TEXTFIELD),
    Null,
    ;

    private List<Class<?>> javaTypes;
    private FormFieldType defaultFormFieldType;

    DataType() {
        javaTypes = Collections.emptyList();
    }

    DataType(FormFieldType formFieldType, Class<?>... javaTypes) {
        this.defaultFormFieldType = formFieldType;
        this.javaTypes = Arrays.stream(javaTypes).toList();
    }
}
