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
@Table(name = "tb_menu")
public class MenuEntity extends BasicEntity {
    @Id
    @NotNull(message = Ai4xExceptionMessage.E000003)
    @Size(max = Length.id, message = Ai4xExceptionMessage.E000004)
    private String id;
    @Size(max = Length.title, message = Ai4xExceptionMessage.E000008)
    @NotNull(message = Ai4xExceptionMessage.E000007)
    private String title;
    @Size(max = Length.tinytext)
    private String url;
    @Size(max = Length.id)
    private String parent;
}
