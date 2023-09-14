package com.kailoslab.ai4x.commons.data.entity;

import com.kailoslab.ai4x.commons.code.Action;
import com.kailoslab.ai4x.commons.code.Level;
import jakarta.annotation.Nonnull;
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
    @Nonnull
    private String caller;
    @Nonnull
    @Enumerated(EnumType.STRING)
    private Level level;
    @Enumerated(EnumType.STRING)
    private Action action;
    @Nonnull
    private String message;
    private String userId;
    private String data;
    @Nonnull
    @CreatedDate
    private LocalDateTime createdDate;

    public Log(String caller, Level level, Action action, String message, String userId, LocalDateTime createdDate) {
        this(caller, level, action, message, userId, createdDate, null);
    }

    public Log(String caller, Level level, Action action, String message, String userId, LocalDateTime createdDate, String data) {
        this.caller = caller;
        this.level = level;
        this.action = action;
        this.message = message;
        this.userId = userId;
        this.data = data;
        this.createdDate = createdDate;
    }
}
