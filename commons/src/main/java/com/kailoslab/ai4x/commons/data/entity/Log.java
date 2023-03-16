package com.kailoslab.ai4x.commons.data.entity;

import com.kailoslab.ai4x.commons.code.Action;
import com.kailoslab.ai4x.commons.code.Level;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tb_log")
public class Log {
    @Id
    private Long id;
    @Enumerated(EnumType.STRING)
    private Level level;
    @Enumerated(EnumType.STRING)
    private Action action;
    private String message;
    private String userId;
    private String data;
    @CreatedDate
    private LocalDateTime createdDate;

    public Log(Level level, Action action, String message, String userId, LocalDateTime createdDate) {
        this(level, action, message, userId, createdDate, null);
    }

    public Log(Level level, Action action, String message, String userId, LocalDateTime createdDate, String data) {
        this.level = level;
        this.action = action;
        this.message = message;
        this.userId = userId;
        this.data = data;
        this.createdDate = createdDate;
    }
}
