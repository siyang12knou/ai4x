package com.kailoslab.ai4x.commons.service.database;

import com.kailoslab.ai4x.commons.data.dto.TableDto;

import java.util.Map;

public interface QueryConsole {
    String getDatasource();
    String getSchema();
    TableDto select(String query, Object... params);
    void save(TableDto tableDto);
    void delete(String tableName, Map<String, Object> condition);
}
