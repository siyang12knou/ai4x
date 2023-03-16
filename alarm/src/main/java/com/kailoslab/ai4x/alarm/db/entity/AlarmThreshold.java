package com.kailoslab.ai4x.alarm.db.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_alarm_threshold")
public class AlarmThreshold {

    @EmbeddedId
    private AlarmThresholdPK id;
    private Float value;

    public AlarmThresholdPK getId() {
        return id;
    }

    public void setId(AlarmThresholdPK id) {
        this.id = id;
    }

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }

}
