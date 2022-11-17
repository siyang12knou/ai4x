package com.kailoslab.ai4x.commons.data.entity;

import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CodePK implements Serializable {
    private String groupId;
    private String codeId;
}