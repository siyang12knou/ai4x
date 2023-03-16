package com.kailoslab.ai4x.alarm;

public enum AlarmLevel {
    Normal("Normal", "#8fbc8f"),
    Warning("Minor", "#ffd700"),
    Error("Major", "#ffa07a"),
    Critical("Critical", "#ff6347");

    private final String alias;
    private final String color;

    AlarmLevel(String alias, String color) {
        this.alias = alias;
        this.color = color;
    }

    public String getAlias() {
        return alias;
    }

    public String getColor() {
        return color;
    }

    public static AlarmLevel getAlarmLevel(int alarmLevelCode) {
        try {
            return AlarmLevel.values()[alarmLevelCode];
        } catch(Throwable ex) {
            return null;
        }
    }

    public static AlarmLevel getAlarmLevelIsNullDefault(int alarmLevelCode) {
        try {
            return AlarmLevel.values()[alarmLevelCode];
        } catch(Throwable ex) {
            return Normal;
        }
    }

}
