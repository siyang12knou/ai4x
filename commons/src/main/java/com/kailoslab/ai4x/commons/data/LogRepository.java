package com.kailoslab.ai4x.commons.data;

import com.kailoslab.ai4x.commons.data.entity.Log;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepository extends CrudRepository<Log, String> {
}
