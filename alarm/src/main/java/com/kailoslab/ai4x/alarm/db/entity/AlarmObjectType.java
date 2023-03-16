package com.kailoslab.ai4x.alarm.db.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "tb_alarm_object_type")
public class AlarmObjectType {

    @EmbeddedId
    private AlarmObjectTypePK id;

    @Column(name = "table_name")
    @JsonProperty("table_name")
    private String tableName;
    @Column(name = "column_name")
    @JsonProperty("column_name")
    private String columnName;
    @Column(name = "alarm_type")
    @JsonProperty("alarm_type")
    private Integer alarmType;
    private String unit;
    @Column(name = "message_format")
    @JsonProperty("message_format")
    private String messageFormat;
    private Boolean enabled;
    @Column(name = "alarm_summary")
    @JsonProperty("alarm_summary")
    private String alarmSummary;
    @Column(name = "alarm_guide")
    @JsonProperty("alarm_gude")
    private String alarmGuide;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumns({
            @JoinColumn(name = "object_type"),
            @JoinColumn(name = "property")
    })
    private List<AlarmThreshold> alarmThreshold;

    public AlarmObjectTypePK getId() {
        return id;
    }

    public void setId(AlarmObjectTypePK id) {
        this.id = id;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public Integer getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(Integer alarmType) {
        this.alarmType = alarmType;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getMessageFormat() {
        return messageFormat;
    }

    public void setMessageFormat(String messageFormat) {
        this.messageFormat = messageFormat;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getAlarmSummary() {
        return alarmSummary;
    }

    public void setAlarmSummary(String alarmSummary) {
        this.alarmSummary = alarmSummary;
    }

    public String getAlarmGuide() {
        return alarmGuide;
    }

    public void setAlarmGuide(String alarmGuide) {
        this.alarmGuide = alarmGuide;
    }

    public List<AlarmThreshold> getAlarmThreshold() {
        return alarmThreshold;
    }

    public void setAlarmThreshold(List<AlarmThreshold> threshold) {
        this.alarmThreshold = threshold;
    }

}
