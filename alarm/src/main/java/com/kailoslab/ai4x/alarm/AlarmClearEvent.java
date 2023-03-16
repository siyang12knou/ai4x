package com.kailoslab.ai4x.alarm;

import com.kailoslab.ai4x.alarm.db.entity.AlarmHistory;
import org.springframework.context.ApplicationEvent;

import java.io.Serial;
import java.util.Collections;
import java.util.List;

public class AlarmClearEvent extends ApplicationEvent {

    @Serial
    private static final long serialVersionUID = 1L;

    public AlarmClearEvent(AlarmHistory alarm) {
        this(Collections.singletonList(alarm));
    }

    public AlarmClearEvent(List<AlarmHistory> alarm) {
        super(alarm);
    }

    public List<AlarmHistory> getAlarmHistory() {
        return (List<AlarmHistory>) getSource();
    }

}
