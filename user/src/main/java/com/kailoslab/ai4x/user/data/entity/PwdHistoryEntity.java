package com.kailoslab.ai4x.user.data.entity;

import com.kailoslab.ai4x.commons.code.Length;
import com.kailoslab.ai4x.commons.exception.Ai4xExceptionMessage;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tb_pwd_history")
@IdClass(PwdHistoryPK.class)
public class PwdHistoryEntity {
    @Id
    @NotNull(message = Ai4xExceptionMessage.E000003)
    @Size(max = Length.id, message = Ai4xExceptionMessage.E000004)
    private String userId;
    @Id
    @NotNull(message = Ai4xExceptionMessage.E000003)
    @Size(max = Length.id, message = Ai4xExceptionMessage.E000004)
    private String groupId;
    @Id
    @NotNull(message = Ai4xExceptionMessage.E000009)
    private String pwd;
    @CreatedDate
    private LocalDateTime createdDate;
}
