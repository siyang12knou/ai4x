package com.kailoslab.ai4x.alarm.db.entity;

public interface AlarmCurrDto {
    String getObject();

    Integer getMaxLevel();

    Integer getMinor();

    Integer getMajor();

    Integer getCritical();

    Integer getWeeklyAlarmCnt();
}
