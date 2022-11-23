package com.kailoslab.ai4x.commons.data.entity;

import com.kailoslab.ai4x.commons.annotation.Title;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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
