package com.kailoslab.ai4x.alarm.db;

import com.kailoslab.ai4x.alarm.db.entity.AlarmRecognitionPK;
import com.kailoslab.ai4x.alarm.db.entity.AlarmRecognition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmRecognitionRepository extends JpaRepository<AlarmRecognition, AlarmRecognitionPK> {

}
