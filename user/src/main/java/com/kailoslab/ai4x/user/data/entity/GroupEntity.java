package com.kailoslab.ai4x.user.data.entity;

import com.kailoslab.ai4x.commons.data.entity.BasicEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@MappedSuperclass
public class GroupEntity extends BasicEntity {
    @Id
    @Column(name = "id", nullable = false)
    private String id;
    private String name;
    private String url;
    private String tel;
    private String email;
    private String zipcode;
    private String address1;
    private String address2;
    private String bizNo;
}
