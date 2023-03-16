package com.kailoslab.ai4x.alarm.db.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_alarm_history")
public class AlarmHistory {

    @Id
    @Column(name = "alarm_id")
    private String id;
    @Column(name = "create_time")
    @JsonProperty("create_time")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    private LocalDateTime createTime;
    @Column(name = "object_type")
    @JsonProperty("object_type")
    private String objectType;
    private String property;
    private String sys = "";
    private String object;
    private Integer level;
    private Float value;

    @Column(name = "nd_id")
    @JsonProperty("nd_id")
    private String ndId;
    private String message;
    private Boolean clear;
    @Column(name = "clear_time")
    @JsonProperty("clear_time")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    private LocalDateTime clearTime;
    private Boolean recognition;

    public AlarmHistory() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getSys() {
        return sys;
    }

    public void setSys(String sys) {
        this.sys = sys;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    public String getNdId() {
        return ndId;
    }

    public void setNdId(String ndId) {
        this.ndId = ndId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getClear() {
        return clear;
    }

    public void setClear(Boolean clear) {
        this.clear = clear;
    }

    public LocalDateTime getClearTime() {
        return clearTime;
    }

    public void setClearTime(LocalDateTime clearTime) {
        this.clearTime = clearTime;
    }

    public Boolean getRecognition() {
        return recognition;
    }

    public void setRecognition(Boolean recognition) {
        this.recognition = recognition;
    }

    @Override
    public String toString() {
        return "AlarmHistory [id=" +
                id +
                ", createTime=" +
                createTime +
                ", objectType=" +
                objectType +
                ", property=" +
                property +
                ", sys=" +
                sys +
                ", object=" +
                object +
                ", level=" +
                level +
                ", value=" +
                value +
                ", ndId=" +
                ndId +
                ", message=" +
                message +
                ", clear=" +
                clear +
                ", clearTime=" +
                clearTime +
                ", recognition=" +
                recognition +
                "]";
    }
}
