package com.kailoslab.ai4x.user.data.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Embeddable
public class PwdHistoryPK implements Serializable {
    private String userId;
    private String groupId;
    private String pwd;
}
