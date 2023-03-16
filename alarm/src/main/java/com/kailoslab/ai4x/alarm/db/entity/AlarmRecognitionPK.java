package com.kailoslab.ai4x.alarm.db.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import java.io.Serial;
import java.io.Serializable;

@Embeddable
public class AlarmRecognitionPK implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "alarm_id")
    @JsonProperty("alarm_id")
    private String alarmId;

    @Column(name = "recognition_id")
    @JsonProperty("recognition_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long recognitionId;

    public String getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(String alarmId) {
        this.alarmId = alarmId;
    }

    public long getRecognitionId() {
        return recognitionId;
    }

    public void setRecognitionId(long recognitionId) {
        this.recognitionId = recognitionId;
    }
}
