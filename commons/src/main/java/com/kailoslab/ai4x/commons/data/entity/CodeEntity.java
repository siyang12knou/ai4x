package com.kailoslab.ai4x.commons.data.entity;

import com.kailoslab.ai4x.commons.utils.Constants;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "tb_code")
public class CodeEntity {
    private String groupId;
    private String codeId;
    private String name;
    private Integer ordinal;
    private Map<String, Object> properties;
    private Boolean deleted;
    private String createdUser;
    private String updatedUser;
    @CreatedDate
    private LocalDateTime createdDate;
    @LastModifiedDate
    private LocalDateTime updatedDate;

    public CodeEntity(String codeId, String name) {
        this.groupId = Constants.DEFAULT_GROUP_ID;
        this.codeId = codeId;
        this.name = name;
        this.ordinal = 0;
    }

    public CodeEntity(String groupId, String codeId, String name, int ordinal) {
        this.groupId = groupId;
        this.codeId = codeId;
        this.name = name;
        this.ordinal = ordinal;
    }
}