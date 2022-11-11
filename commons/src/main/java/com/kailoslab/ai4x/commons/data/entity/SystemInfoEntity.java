package com.kailoslab.ai4x.commons.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SystemInfoEntity extends BasicEntity {
    @JsonIgnore
    private UserEntity createdUser;
    @JsonIgnore
    private UserEntity updatedUser;
}