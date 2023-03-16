package com.kailoslab.ai4x.alarm;

import com.kailoslab.ai4x.alarm.db.entity.AlarmHistory;
import org.springframework.context.ApplicationEvent;

import java.io.Serial;

public class AlarmEvent extends ApplicationEvent {

    @Serial
    private static final long serialVersionUID = 1L;

    public AlarmEvent(AlarmHistory source) {
        super(source);
    }

    public AlarmHistory getAlarmHistory() {
        return (AlarmHistory) getSource();
    }

}
