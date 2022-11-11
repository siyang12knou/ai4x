package com.kailoslab.ai4x.logic.data.entity;

import com.kailoslab.ai4x.logic.code.ProgramLang;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Setter
@Getter
@Table(name = "tb_ai4x_logic_fragment")
public class LogicFragmentEntity {
    @Id
    private String id;
    private ProgramLang programLang;
    private String source;
    private int ordinal;
    private LogicEntity logic;
}
