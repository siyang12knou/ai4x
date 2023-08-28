package com.kailoslab.ai4x.user.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kailoslab.ai4x.commons.data.entity.BasicEntity;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@MappedSuperclass
public class UserEntity extends BasicEntity {
    @Id
    private String id;
    @JsonIgnore
    private String password;

    private String name;
    private String enName;
    private String email;
    private String phone;

    private String country;
    private String language;
    private String timeZone;
    private Boolean enabled = true;

    public UserEntity(String id, String password, String email) {
        this(id, password, email, "", id);
    }

    public UserEntity(String id, String password, String email, String phone, String name) {
        this.id = id;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.name = name;
    }
}