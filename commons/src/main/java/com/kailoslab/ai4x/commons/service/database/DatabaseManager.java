package com.kailoslab.ai4x.commons.service.database;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class DatabaseManager {

    private final List<DatabaseService<?>> databaseServices;

    private final DummyDatabaseService dummyDatabaseService = new DummyDatabaseService();

    public DatabaseService getDataService(String dbms){
        if(!ObjectUtils.isEmpty(databaseServices)) {
            Optional<DatabaseService<?>> databaseServiceOptional =
                    databaseServices.stream().filter(
                            databaseService -> StringUtils.equals(databaseService.getSupportedDbms(), dbms)
                    ).findFirst();
            if(databaseServiceOptional.isPresent()) {
                return databaseServiceOptional.get();
            }
        }

        return dummyDatabaseService;
    }
}
