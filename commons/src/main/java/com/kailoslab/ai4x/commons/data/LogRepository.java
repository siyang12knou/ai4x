package com.kailoslab.ai4x.commons.data;

import com.kailoslab.ai4x.commons.data.entity.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepository extends JpaRepository<Log, String> {
}
