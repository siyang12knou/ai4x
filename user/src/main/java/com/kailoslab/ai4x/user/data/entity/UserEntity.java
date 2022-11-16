package com.kailoslab.ai4x.user.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kailoslab.ai4x.user.code.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Table(name = "tb_ai4x_user")
public class UserEntity extends PrivateInfoEntity {
    @Id
    private String id;
    @JsonIgnore
    private String password;
    private String name;
    private String email;
    private String tel;
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