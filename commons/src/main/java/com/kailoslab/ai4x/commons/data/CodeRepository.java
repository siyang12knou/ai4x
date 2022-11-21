package com.kailoslab.ai4x.commons.data;

import com.kailoslab.ai4x.commons.data.entity.CodeEntity;
import com.kailoslab.ai4x.commons.data.entity.CodePK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CodeRepository extends JpaRepository<CodeEntity, CodePK> {

    List<CodeEntity> findByGroupIdAndDeletedFalseOrderByOrdinal(String groupId);
    List<CodeEntity> findByGroupIdAndCodeIdInAndDeletedFalseOrderByOrdinal(String groupId, List<String> codeId);
    Optional<CodeEntity> findFirstByGroupIdAndDeletedFalseOrderByOrdinalDesc(String groupId);
    Optional<CodeEntity> findByGroupIdAndCodeIdAndDeletedFalse(String groupId, String codeId);
}