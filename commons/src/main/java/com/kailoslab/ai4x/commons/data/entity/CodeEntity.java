package com.kailoslab.ai4x.commons.data.entity;

import com.kailoslab.ai4x.commons.data.converter.MapConverter;
import com.kailoslab.ai4x.commons.utils.Constants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
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

    public CodeEntity(String codeId, String name) {
        this(Constants.DEFAULT_GROUP_ID, codeId, name, Constants.ORDINAL_START);
    }

    public CodeEntity(String groupId, String codeId, String name, int ordinal) {
        this.groupId = groupId;
        this.codeId = codeId;
        this.name = name;
        this.ordinal = ordinal;
    }
}