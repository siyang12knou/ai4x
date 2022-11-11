package com.kailoslab.ai4x.commons.service.database;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;

public interface DatabaseService<T> {
    String getSupportedDbms();
    boolean createDatasource(String name, T properties);
    List<DataSource> getDataSources();
    default DataSource getDataSource(String name) {
        Optional<DataSource> dataSourceOptional = getDataSources().stream().filter(dataSource -> StringUtils.equals(dataSource.getName(), name)).findFirst();
        return dataSourceOptional.orElse(null);
    }

    default QueryConsole getQueryConsole(String dataSourceName) {
        DataSource dataSource = getDataSource(dataSourceName);
        if(ObjectUtils.isEmpty(dataSource)) {
            return null;
        } else {
            return dataSource.getQueryConsole();
        }
    }

    default QueryConsole getQueryConsole(String dataSourceName, String schemaName) {
        DataSource dataSource = getDataSource(dataSourceName);
        if(ObjectUtils.isEmpty(dataSource)) {
            return null;
        } else {
            return dataSource.getQueryConsole(schemaName);
        }
    }
}
