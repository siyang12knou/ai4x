package com.kailoslab.ai4x.user.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kailoslab.ai4x.commons.data.converter.ListConverter;
import com.kailoslab.ai4x.user.code.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tb_user")
public class UserEntity extends PrivateInfoEntity {
    @Id
    private String id;
    @JsonIgnore
    private String password;
    private String name;
    private String email;
    private String tel;
    @Convert(converter = ListConverter.class)
    private List<String> role;
    private LocalDate contractStartDate;
    private LocalDate contractEndDate;
    private Boolean enabled = true;

    public UserEntity(String id, String password, String email) {
        this(id, password, email, "", id, Collections.singletonList(Role.ADMIN.name()));
    }

    public UserEntity(String id, String password, String email, String tel, String name, List<String> role) {
        super(id, password, email, tel);
        this.name = name;
        this.role = role;
    }
}