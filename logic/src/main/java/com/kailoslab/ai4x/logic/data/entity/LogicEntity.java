package com.kailoslab.ai4x.logic.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kailoslab.ai4x.commons.data.entity.SystemInfoEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name = "tb_ai4x_logic")
public class LogicEntity extends SystemInfoEntity {
    @Id
    private String id;
    private String name;
    @JsonIgnore
    private List<LogicFragmentEntity> fragments;
}
