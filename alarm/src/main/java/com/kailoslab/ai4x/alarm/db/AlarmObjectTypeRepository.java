package com.kailoslab.ai4x.alarm.db;

import com.kailoslab.ai4x.alarm.db.entity.AlarmObjectType;
import com.kailoslab.ai4x.alarm.db.entity.AlarmObjectTypePK;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlarmObjectTypeRepository extends JpaRepository<AlarmObjectType, AlarmObjectTypePK> {

    List<AlarmObjectType> findByTableName(String tableName);

    boolean existsByTableNameAndAlarmTypeNot(String tableName, Integer alarmType);
    boolean existsByTableNameAndAlarmType(String tableName, Integer alarmType);

}
