package com.kailoslab.ai4x.alarm.db.entity;

import com.kailoslab.ai4x.commons.data.entity.BasicEntity;
import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "tb_alarm_measure")
public class AlarmMeasure extends BasicEntity {

    @EmbeddedId
    private AlarmMeasurePK id;

    private String contents;

    @Column(name = "measure_user")
    @JsonProperty("measure_user")
    private String measureUser;

    public AlarmMeasurePK getId() {
        return id;
    }

    public void setId(AlarmMeasurePK id) {
        this.id = id;
    }

    public String getMeasureUser() {
        return measureUser;
    }

    public void setMeasureUser(String measureUser) {
        this.measureUser = measureUser;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

}
