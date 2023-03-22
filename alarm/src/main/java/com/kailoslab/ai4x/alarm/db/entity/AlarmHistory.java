package com.kailoslab.ai4x.alarm.db.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import java.time.LocalDateTime;

@Entity
@Table(name = "tb_alarm_history")
@Getter
@Setter
@ToString
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

    @Column(name = "domain")
    @JsonProperty("domain")
    private String domain;
    private String message;
    private Boolean clear;
    @Column(name = "clear_time")
    @JsonProperty("clear_time")
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    private LocalDateTime clearTime;
    private Boolean recognition;

}
