package com.kailoslab.ai4x.alarm;

import com.kailoslab.ai4x.alarm.db.AlarmObjectTypeRepository;
import com.kailoslab.ai4x.alarm.db.entity.AlarmObjectType;
import com.kailoslab.ai4x.alarm.db.entity.AlarmObjectTypePK;
import com.kailoslab.ai4x.alarm.db.entity.AlarmThreshold;
import jakarta.persistence.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AlarmUtils {
    private final AlarmObjectTypeRepository alarmObjectTypeRepository;

    @Autowired
    public AlarmUtils(AlarmObjectTypeRepository alarmObjectTypeRepository) {
        this.alarmObjectTypeRepository = alarmObjectTypeRepository;
    }

    public AlarmObjectType getAlarmObjectType(ObjectType objectType, ObjectProperty property) {
        return getAlarmObjectType(objectType.name(), property.name());
    }

    public AlarmObjectType getAlarmObjectType(String objectType, String property) {
        AlarmObjectTypePK id = new AlarmObjectTypePK();
        id.setObjectType(objectType);
        id.setProperty(property);
        return alarmObjectTypeRepository.findById(id).orElse(null);
    }

    public List<AlarmObjectType> getAlarmObjectTypes(Object entity) {
        if (entity == null) {
            return null;
        }

        Table table = entity.getClass().getDeclaredAnnotation(Table.class);
        String tableName;
        if (table != null) {
            tableName = table.name();
        } else {
            tableName = entity.getClass().getSimpleName();
        }

        return alarmObjectTypeRepository.findByTableName(tableName);
    }

    public int getAlarmLevelForBoolean(float value, float criticalValue) {
        if (value == criticalValue) {
            return AlarmLevel.Critical.ordinal();
        } else {
            return AlarmLevel.Normal.ordinal();
        }
    }

    public int getAlarmLevelForBoolean(boolean bool, boolean criticalValue) {
        if (bool == criticalValue) {
            return AlarmLevel.Critical.ordinal();
        } else {
            return AlarmLevel.Normal.ordinal();
        }
    }

    public int getAlarmLevelForNumber(Number num, AlarmObjectType alarmObjectType) {
        return getAlarmLevelForNumber(num.floatValue(),
                alarmObjectType == null ? null : alarmObjectType.getAlarmThreshold());
    }

    public static int getAlarmLevelForNumber(float num, List<AlarmThreshold> threshold) {
        int rtn = -1;

        if (threshold == null || threshold.size() == 0) {
            return AlarmLevel.Normal.ordinal();
        }

        for (AlarmThreshold alarmLimit : threshold) {
            if (alarmLimit.getValue() > num) {
                break;
            } else if (alarmLimit.getValue() <= num) {
                rtn = alarmLimit.getId().getLevel();
            }
        }

        if (rtn == -1) {
            return AlarmLevel.Normal.ordinal();
        }
        return rtn;
    }
}
