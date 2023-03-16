package com.kailoslab.ai4x.alarm;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ObjectType {
    CONTROLLER,
    SWITCH,
    PORT,
    FAN,
    POWER,
    SLICE_BRIDGE,
    HOST;

    @JsonValue
    public int toValue() {
        return ordinal();
    }
}
