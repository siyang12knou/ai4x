package com.kailoslab.ai4x.commons.data.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@NoArgsConstructor
@Setter
@Getter
@Table(name = "tb_title")
public class TitleEntity {
    @Id
    private String id;
    private String titleKey;
    private String lang;
    private String country;
    private String title;
}
