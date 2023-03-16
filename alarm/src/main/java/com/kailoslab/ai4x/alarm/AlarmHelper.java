package com.kailoslab.ai4x.alarm;

import com.kailoslab.ai4x.alarm.db.entity.AlarmHistory;
import com.kailoslab.ai4x.alarm.db.entity.AlarmObjectType;

public interface AlarmHelper {

    boolean isSupportedObjectType(AlarmObjectType objectType);

    default AlarmHistory beforeSaveAlarmHistory(AlarmRequestEvent event, AlarmObjectType objectType, AlarmHistory history) {
        return history;
    }

    default AlarmHistory afterSaveAlarmHistory(AlarmRequestEvent event, AlarmObjectType objectType, AlarmHistory history) {
        return history;
    }

}
