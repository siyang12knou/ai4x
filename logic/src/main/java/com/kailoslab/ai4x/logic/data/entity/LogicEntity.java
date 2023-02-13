package com.kailoslab.ai4x.logic.data.entity;

import com.kailoslab.ai4x.commons.data.converter.MapConverter;
import com.kailoslab.ai4x.logic.code.Language;
import com.kailoslab.ai4x.user.data.entity.SystemInfoEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "tb_logic")
public class LogicEntity extends SystemInfoEntity {
    @Id
    private String id;
    private String name;
    private String target;
    @Enumerated
    private Language language;
    @Convert(converter = MapConverter.class)
    private Map<String, Object> defaultValue;
}
