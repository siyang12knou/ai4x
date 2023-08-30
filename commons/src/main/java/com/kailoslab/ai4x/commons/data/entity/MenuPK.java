package com.kailoslab.ai4x.commons.data.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Embeddable
@EqualsAndHashCode
public class MenuPK implements Serializable {
    private String service;
    private String path;
}