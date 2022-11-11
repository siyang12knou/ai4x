package com.kailoslab.ai4x.commons.data.dto;

import com.kailoslab.ai4x.commons.code.DataType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class TableColumnDto {
    private String name;
    private DataType dataType;
    private boolean pk;
}
