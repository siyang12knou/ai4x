package com.kailoslab.ai4x.commons.data.entity;

import com.kailoslab.ai4x.commons.code.Length;
import com.kailoslab.ai4x.commons.exception.Ai4xExceptionMessage;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "tb_menu_use")
@IdClass(MenuUsePK.class)
public class MenuUseEntity {
    @Id
    @NotNull(message = Ai4xExceptionMessage.E000003)
    @Size(max = Length.id, message = Ai4xExceptionMessage.E000004)
    private String service;
    @Id
    @NotNull(message = Ai4xExceptionMessage.E000003)
    @Size(max = Length.path, message = Ai4xExceptionMessage.E000004)
    private String path;
    @Id
    @NotNull(message = Ai4xExceptionMessage.E000003)
    @Size(max = Length.name, message = Ai4xExceptionMessage.E000004)
    private String ownId;

    private Boolean used = true;
}
