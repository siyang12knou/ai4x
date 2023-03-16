package com.kailoslab.ai4x.alarm.db.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kailoslab.ai4x.commons.data.entity.BasicEntity;
import jakarta.persistence.*;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_alarm_recognition")
public class AlarmRecognition extends BasicEntity {


    @EmbeddedId
    private AlarmRecognitionPK id;
    private String contents;
    @Column(name = "recognition_user")
    @JsonProperty("recognition_user")
    private String recognitionUser;
    @Column(name = "recognition_type")
    @JsonProperty("recognition_type")
    private Byte recognitionType;
    @Column(name = "recognition_flag")
    @JsonProperty("recognition_flag")
    private Boolean recognitionFlag;
    @Column(name = "end_time")
    @JsonProperty("end_time")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    private LocalDateTime endTime;

    public AlarmRecognitionPK getId() {
        return id;
    }

    public void setId(AlarmRecognitionPK id) {
        this.id = id;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getRecognitionUser() {
        return recognitionUser;
    }

    public void setRecognitionUser(String recognitionUser) {
        this.recognitionUser = recognitionUser;
    }

    public Byte getRecognitionType() {
        return recognitionType;
    }

    public void setRecognitionType(Byte recognitionType) {
        this.recognitionType = recognitionType;
    }

    public Boolean getRecognitionFlag() {
        return recognitionFlag;
    }

    public void setRecognitionFlag(Boolean recognitionFlag) {
        this.recognitionFlag = recognitionFlag;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

}
