package com.kailoslab.ai4x.batch.data;

import com.kailoslab.ai4x.batch.data.entity.BatchJobInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BatchJobInfoRepository extends CrudRepository<BatchJobInfo, String> {
    default String generateId(String jobGroup, String jobName) {
        return "%s-%s".formatted(StringUtils.trimToEmpty(jobGroup), StringUtils.trimToEmpty(jobName));
    }

    default Optional<BatchJobInfo> findByJobGroupAndJobName(String jobGroup, String jobName) {
        return findById(generateId(jobGroup, jobName));
    }

    default List<BatchJobInfo> findAllByJobGroup(String jobGroup) {
        return findAllByJobIdLike(jobGroup);
    }

    List<BatchJobInfo> findAllByJobIdLike(String jobGroup);
}
