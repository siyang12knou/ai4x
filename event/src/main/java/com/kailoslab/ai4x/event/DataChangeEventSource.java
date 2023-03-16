package com.kailoslab.ai4x.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kailoslab.ai4x.event.code.Op;

import java.time.LocalDateTime;

public class DataChangeEventSource {
    public final LocalDateTime timestamp;
    public final  String entityClassName;
    public final Op op;
    public final  Object entity;

    public DataChangeEventSource(Op op, Object entity) {
        this.timestamp = LocalDateTime.now();
        this.entityClassName = entity.getClass().getName();
        this.op = op;
        this.entity = entity;
    }

    @JsonCreator
    public DataChangeEventSource(@JsonProperty("timestamp") LocalDateTime timestamp,
                                 @JsonProperty("entityClassName") String entityClassName,
                                 @JsonProperty("op") Op op,
                                 @JsonProperty("entity") Object entity) {
        this.timestamp = timestamp;
        this.entityClassName = entityClassName;
        this.op = op;
        this.entity = entity;
    }
}
