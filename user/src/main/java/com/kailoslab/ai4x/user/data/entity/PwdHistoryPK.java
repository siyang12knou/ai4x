package com.kailoslab.ai4x.user.data.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Embeddable
@EqualsAndHashCode
public class PwdHistoryPK implements Serializable {
    private String userId;
    private String groupId;
    private String pwd;
}
