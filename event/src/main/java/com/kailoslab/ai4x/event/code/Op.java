package com.kailoslab.ai4x.event.code;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Op {
    insert,
    update,
    delete,
    save,
    login,
    logout,
    approve,
    reset,
    send
    ;

    @JsonValue
    public int toValue() {
        return ordinal();
    }
}
