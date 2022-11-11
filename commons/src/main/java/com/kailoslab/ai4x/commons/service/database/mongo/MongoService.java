package com.kailoslab.ai4x.commons.service.database.mongo;

import com.kailoslab.ai4x.commons.service.database.DataSource;
import com.kailoslab.ai4x.commons.service.database.DatabaseService;

import java.util.List;

public class MongoService implements DatabaseService {


    @Override
    public String getSupportedDbms() {
        return null;
    }

    @Override
    public boolean createDatasource(String name, Object properties) {
        return false;
    }

    @Override
    public List<DataSource> getDataSources() {
        return null;
    }
}
