package com.kailoslab.ai4x.alarm.db.entity;

public interface AlarmReportDto {
    String getObject();

    Integer getCritical();

    Integer getMajor();

    Integer getMinor();

    Integer getClearAuto();

    Integer getClearManual();

    Integer getUnclear();
}
