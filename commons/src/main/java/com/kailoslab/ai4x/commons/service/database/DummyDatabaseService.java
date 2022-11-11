package com.kailoslab.ai4x.commons.service.database;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DummyDatabaseService implements DatabaseService<Map<String, Object>> {


    @Override
    public String getSupportedDbms() {
        return null;
    }

    @Override
    public boolean createDatasource(String name, Map<String, Object> properties) {
        return false;
    }

    @Override
    public List<DataSource> getDataSources() {
        return Collections.emptyList();
    }
}
