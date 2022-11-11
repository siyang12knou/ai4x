package com.kailoslab.ai4x.commons.data.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class PropertiesFormatEntryDto {
    private String title;
    private String name;
    private String inputFieldType;
    private String valueType;
    private List<CodeDto> codes;
}
