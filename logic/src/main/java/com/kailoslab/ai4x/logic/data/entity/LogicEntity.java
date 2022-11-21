package com.kailoslab.ai4x.logic.data.entity;

import com.kailoslab.ai4x.user.data.entity.SystemInfoEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "tb_logic")
public class LogicEntity extends SystemInfoEntity {
    @Id
    private String id;
    private String name;
}
