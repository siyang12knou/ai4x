package com.kailoslab.ai4x.commons.data.entity;

import lombok.*;

import javax.persistence.Embeddable;
import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Embeddable
public class CodePK implements Serializable {
    private String groupId;
    private String codeId;
}