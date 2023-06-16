package com.kailoslab.ai4x.commons.data.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CodeDto {
    private String codeId;
    private String name;
    private Boolean defaultCode;
}
