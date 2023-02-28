package com.kailoslab.ai4x.commons.data.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "tb_title")
public class TitleEntity {
    @Id
    private String id;
    private String titleKey;
    private String lang;
    private String country;
    private String title;

    public TitleEntity(String id) {
        this.id = id;
    }
}
