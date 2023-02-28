package com.kailoslab.ai4x.commons.data.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "tb_properties")
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
