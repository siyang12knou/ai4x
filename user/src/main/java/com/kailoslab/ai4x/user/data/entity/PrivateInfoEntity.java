package com.kailoslab.ai4x.user.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kailoslab.ai4x.commons.data.entity.BasicEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@NoArgsConstructor
@Getter
@Setter
@MappedSuperclass
public class PrivateInfoEntity extends BasicEntity {
    @Id
    private String id;
    @JsonIgnore
    private String password;
    private String email;
    private String tel;
    private Boolean enabled = true;

    public PrivateInfoEntity(String id, String password, String email, String tel) {
        this.id = id;
        this.password = password;
        this.email = email;
        this.tel = tel;
    }
}