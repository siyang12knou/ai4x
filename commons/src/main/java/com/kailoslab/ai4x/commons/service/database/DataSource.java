package com.kailoslab.ai4x.commons.service.database;

import java.util.List;

public interface DataSource {
    String getName();
    List<String> getSchemaNames(String dataSourceName);
    void setDefaultSchema(String schemaName);

    String getDefaultSchema();
    default QueryConsole getQueryConsole() {
        return getQueryConsole(getDefaultSchema());
    }

    QueryConsole getQueryConsole(String schemaName);
}
