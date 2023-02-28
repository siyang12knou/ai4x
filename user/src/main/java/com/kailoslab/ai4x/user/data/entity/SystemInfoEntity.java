package com.kailoslab.ai4x.user.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kailoslab.ai4x.commons.data.entity.BasicEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@MappedSuperclass
public class SystemInfoEntity extends BasicEntity {
    @ManyToOne(targetEntity = UserEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "created_user")
    @JsonIgnore
    private UserEntity createdUser;
    @ManyToOne(targetEntity = UserEntity.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_user")
    @JsonIgnore
    private UserEntity updatedUser;
}