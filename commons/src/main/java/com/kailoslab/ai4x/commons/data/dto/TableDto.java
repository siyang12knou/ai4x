package com.kailoslab.ai4x.commons.data.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class TableDto {
    private String schemaName;
    private String tableName;
    private List<TableColumnDto> columns;
    private List<Map<String, Object>> rows;
}
