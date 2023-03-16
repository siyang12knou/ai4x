package com.kailoslab.ai4x.alarm.db;

import com.kailoslab.ai4x.alarm.db.entity.AlarmMeasure;
import com.kailoslab.ai4x.alarm.db.entity.AlarmMeasurePK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmMeasureRepository extends JpaRepository<AlarmMeasure, AlarmMeasurePK> {

}
