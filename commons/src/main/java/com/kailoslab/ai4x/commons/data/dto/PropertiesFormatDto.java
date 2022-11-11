package com.kailoslab.ai4x.commons.data.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class PropertiesFormatDto {
    private String title;
    private String serviceName;
    private String instanceName;
    private String propertiesName;
    @Getter
    private List<PropertiesFormatEntryDto> entries = new ArrayList<>();
}
