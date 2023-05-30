package com.kailoslab.ai4x.user.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kailoslab.ai4x.commons.data.entity.BasicEntity;
import com.kailoslab.ai4x.user.code.AuthNoType;
import dev.hilla.Nonnull;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

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
    private String email;
    private String phone;

    private Integer authNo;
    private LocalDateTime authNoCreatedDate;
    @Enumerated(EnumType.STRING)
    private AuthNoType authNoType;
    private Boolean enabled = true;
    @Lob
    @Column(length = 1000000)
    private byte @Nonnull [] profilePicture;

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