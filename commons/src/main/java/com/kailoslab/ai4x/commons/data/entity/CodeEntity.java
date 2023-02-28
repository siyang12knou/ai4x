package com.kailoslab.ai4x.commons.data.entity;

import com.kailoslab.ai4x.commons.data.converter.MapConverter;
import com.kailoslab.ai4x.commons.utils.Constants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@IdClass(CodePK.class)
@Table(name = "tb_code")
public class CodeEntity extends BasicEntity {
    @Id
    private String groupId;
    @Id
    private String codeId;
    private String name;
    private Integer ordinal;

    @Convert(converter = MapConverter.class)
    private Map<String, Object> properties;
    private String createdUser = Constants.SYSTEM_ID;
    private String modifiedUser = Constants.SYSTEM_ID;

    public CodeEntity(CodePK pk) {
        this(pk, pk.getCodeId(), Constants.ORDINAL_START);
    }

    public CodeEntity(CodePK pk, String name, int ordinal) {
        this.groupId = pk.getGroupId();
        this.codeId = pk.getCodeId();
        this.name = name;
        this.ordinal = ordinal;
    }
}