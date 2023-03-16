package com.kailoslab.ai4x.alarm.db;

import com.kailoslab.ai4x.alarm.db.entity.AlarmCountDto;
import com.kailoslab.ai4x.alarm.db.entity.AlarmCurrDto;
import com.kailoslab.ai4x.alarm.db.entity.AlarmHistory;
import com.kailoslab.ai4x.alarm.db.entity.AlarmReportDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AlarmHistoryRepository extends JpaRepository<AlarmHistory, String> {

    List<AlarmHistory> findByObjectTypeAndPropertyAndObjectAndClearFalseOrderByCreateTimeDesc(
            String objectType, String property, String object);
    List<AlarmHistory> findByObjectTypeAndObjectAndClearFalseOrderByCreateTimeDesc(
            String objectType, String object);
    List<AlarmHistory> findByObjectTypeAndObjectStartsWithAndClearFalseOrderByCreateTime(
            String objectType, String object);

//    @Query(value = """
//            select object as Object,\s
//            count(IF(tah.level = 1, tah.level, NULL)) as Minor,\s
//            count(IF(tah.level = 2, tah.level, NULL)) as Major,\s
//            count(IF(tah.level = 3, tah.level, NULL)) as Critical,\s
//            count(case when tam.measure_id is null and tah.clear = 1 then 1 end) as ClearAuto,\s
//            count(case when tam.measure_id is not null and tah.clear = 1 then 1 end) as ClearManual,\s
//            count(case when tah.clear <> 1 then 1 end) as Unclear
//            from tb_alarm_history as tah left join\s
//            (select * from tb_alarm_measure) as tam on tam.alarm_id = tah.alarm_id\s
//            where tah.object in (
//            select port_id from tb_port union
//            select switch_id from tb_switch union
//            select controller_id from tb_controller)\s
//            and date(tah.create_time) = curdate() - 1
//            group by tah.object""", nativeQuery = true)
    List<AlarmReportDto> getDailyAlarmReport();

//    @Query(value = """
//            select object as Object,\s
//            count(IF(tah.level = 1, tah.level, NULL)) as Minor,\s
//            count(IF(tah.level = 2, tah.level, NULL)) as Major,\s
//            count(IF(tah.level = 3, tah.level, NULL)) as Critical,\s
//            count(case when tam.measure_id is null and tah.clear = 1 then 1 end) as ClearAuto,\s
//            count(case when tam.measure_id is not null and tah.clear = 1 then 1 end) as ClearManual,\s
//            count(case when tah.clear <> 1 then 1 end) as Unclear
//            from tb_alarm_history as tah left join\s
//            (select * from tb_alarm_measure) as tam on tam.alarm_id = tah.alarm_id\s
//            where tah.object in (
//            select port_id from tb_port union
//            select switch_id from tb_switch union
//            select controller_id from tb_controller)\s
//            and weekofyear(tah.create_time) = weekofyear(curdate()) - 1 and year(tah.create_time) = year(curdate())
//            group by tah.object""", nativeQuery = true)
    List<AlarmReportDto> getWeeklyAlarmReport();

//    @Query(value = """
//            select object as Object,\s
//            count(IF(tah.level = 1, tah.level, NULL)) as Minor,\s
//            count(IF(tah.level = 2, tah.level, NULL)) as Major,\s
//            count(IF(tah.level = 3, tah.level, NULL)) as Critical,\s
//            count(case when tam.measure_id is null and tah.clear = 1 then 1 end) as ClearAuto,\s
//            count(case when tam.measure_id is not null and tah.clear = 1 then 1 end) as ClearManual,\s
//            count(case when tah.clear <> 1 then 1 end) as Unclear
//            from tb_alarm_history as tah left join\s
//            (select * from tb_alarm_measure) as tam on tam.alarm_id = tah.alarm_id\s
//            where tah.object in (
//            select port_id from tb_port union
//            select switch_id from tb_switch union
//            select controller_id from tb_controller)\s
//            and month(tah.create_time) = month(curdate()) - 1 and year(tah.create_time) = year(curdate())
//            group by tah.object""", nativeQuery = true)
    List<AlarmReportDto> getMonthlyAlarmReport();

//    @Query(value = """
//            select object as Object,\s
//            IFNULL(max(case when tah.clear <> 1 then tah.level end) , 0) as MaxLevel,\s
//            count(IF(tah.level = 1, tah.level, NULL)) as Minor,\s
//            count(IF(tah.level = 2, tah.level, NULL)) as Major,\s
//            count(IF(tah.level = 3, tah.level, NULL)) as Critical,\s
//            count(case when YEARWEEK(tah.create_time) = YEARWEEK(now()) then 1 end) as WeeklyAlarmCnt\s
//            from tb_alarm_history as tah\s
//            where tah.object in (
//            select port_id from tb_port union
//            select switch_id from tb_switch union
//            select controller_id from tb_controller) and tah.clear <> 1 group by tah.object;""", nativeQuery = true)
    List<AlarmCurrDto> getCurrAlarm();

    @Query(value = """
            select COUNT(level)                      AS totalCount
                 , COUNT(IF(level = 1, level, NULL)) AS warningCount
                 , COUNT(IF(level = 2, level, null)) AS errorCount
                 , COUNT(IF(level = 3, level, null)) AS criticalCount
            from tb_alarm_history
            where clear <> 1
              and (object = :object or sys = :object) limit 1""", nativeQuery = true)
    AlarmCountDto getAlarmCountBySysOrObject(String object);

    @Query(value = "select count(*) from tb_alarm_history  " +
            "where clear <> 1 and (object = :object or sys = :object) limit 1;", nativeQuery = true)
    Integer isExistsAlarmBySysOrObject(String object);

    @Query(value = "select " +
            "   count( ah.level )                           as totalCount " +
            " , count( if( ah.level = 1 , ah.level, null) ) as warningCount " +
            " , count( if( ah.level = 2 , ah.level, null) ) as errorCount " +
            " , count( if( ah.level = 3 , ah.level, null) ) as criticalCount " +
            "FROM tb_alarm_history ah " +
            "where ah.clear = 0 " +
            "and weekofyear(ah.create_time) = weekofyear(curdate()) and year(ah.create_time) = year(curdate());", nativeQuery = true)
    AlarmCountDto getWeeklyAlarmCount();
}
