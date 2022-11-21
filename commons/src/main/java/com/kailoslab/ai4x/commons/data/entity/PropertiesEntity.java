package com.kailoslab.ai4x.commons.data.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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
