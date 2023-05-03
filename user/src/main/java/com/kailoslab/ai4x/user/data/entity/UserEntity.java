package com.kailoslab.ai4x.user.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kailoslab.ai4x.commons.data.converter.ListConverter;
import com.kailoslab.ai4x.commons.data.entity.BasicEntity;
import com.kailoslab.ai4x.user.code.AuthNoType;
import com.kailoslab.ai4x.user.code.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tb_user")
@Inheritance
public class UserEntity extends BasicEntity {
    @Id
    private String id;
    @JsonIgnore
    private String password;
    private String name;
    private String email;
    private String phone;

    @Convert(converter = ListConverter.class)
    private List<String> role;
    private Integer authNo;
    private LocalDateTime authNoCreatedDate;
    @Enumerated
    private AuthNoType authNoType;
    private Boolean enabled = true;

    public UserEntity(String id, String password, String email) {
        this(id, password, email, "", id, Collections.singletonList(Role.ADMIN.name()));
    }

    public UserEntity(String id, String password, String email, String phone, String name, List<String> role) {
        this.id = id;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.name = name;
        this.role = role;
    }
}