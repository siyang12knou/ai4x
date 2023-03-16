package com.kailoslab.ai4x.alarm.db.entity;

public interface AlarmCountDto {

    Integer getTotalCount();
    Integer getCriticalCount();
    Integer getErrorCount();
    Integer getWarningCount();

}
