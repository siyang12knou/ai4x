package com.kailoslab.ai4x.alarm.db;

import com.kailoslab.ai4x.alarm.db.entity.AlarmThreshold;
import com.kailoslab.ai4x.alarm.db.entity.AlarmThresholdPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmThresholdRepository extends JpaRepository<AlarmThreshold, AlarmThresholdPK> {

}
