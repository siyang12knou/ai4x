package com.kailoslab.ai4x.commons.data.entity;

import com.kailoslab.ai4x.commons.code.Length;
import com.kailoslab.ai4x.commons.exception.Ai4xExceptionMessage;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
@Table(name = "tb_role")
public class RoleEntity extends BasicEntity {
    @Id
    @NotNull(message = Ai4xExceptionMessage.E000003)
    @Size(max = Length.id, message = Ai4xExceptionMessage.E000004)
    private String id;
    @Size(max = Length.name, message = Ai4xExceptionMessage.E000006)
    @NotNull(message = Ai4xExceptionMessage.E000005)
    private String name;

    public RoleEntity(String id, String name) {
        this.id = id;
        this.name = name;
    }
}
