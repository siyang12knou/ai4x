package com.kailoslab.ai4x.user.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kailoslab.ai4x.commons.code.UserType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

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
    private UserType userType;
    private Boolean enabled = true;

    public UserEntity(String id, String password, String email) {
        this(id, password, email, "", id, UserType.ADMIN);
    }

    public UserEntity(String id, String password, String email, String tel, String name, UserType userType) {
        super(id, password, email, tel);
        this.name = name;
        this.userType = userType;
    }
}