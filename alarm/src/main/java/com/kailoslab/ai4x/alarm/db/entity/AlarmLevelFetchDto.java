package com.kailoslab.ai4x.alarm.db.entity;

public interface AlarmLevelFetchDto {
    String getObject();

    Integer getCritical();

    Integer getMajor();

    Integer getMinor();

}
