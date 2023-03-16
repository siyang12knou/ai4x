package com.kailoslab.ai4x.alarm.db.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import java.io.Serial;
import java.io.Serializable;

@Embeddable
public class AlarmMeasurePK implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "alarm_id")
    @JsonProperty("alarm_id")
    private String alarmId;

    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "measure_id")
    @JsonProperty("measure_id")
    private long measureId;

    public String getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(String alarmId) {
        this.alarmId = alarmId;
    }

    public long getMeasureId() {
        return measureId;
    }

    public void setMeasureId(long measureId) {
        this.measureId = measureId;
    }
}
