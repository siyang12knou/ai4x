package com.kailoslab.ai4x.commons.data.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@NoArgsConstructor
@Setter
@Getter
@Table(name = "tb_ai4x_properties")
public class PropertiesEntity {
    @Id
    private String id;
    private String serviceName;
    private String instanceName;
    private String propertiesName;
    private String propertiesType;
    private String properties;
    private Integer ordinal;
}
