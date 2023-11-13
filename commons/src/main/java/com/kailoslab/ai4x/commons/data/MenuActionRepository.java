package com.kailoslab.ai4x.commons.data;

import com.kailoslab.ai4x.commons.data.entity.MenuActionEntity;
import com.kailoslab.ai4x.commons.data.entity.MenuActionPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuActionRepository extends JpaRepository<MenuActionEntity, MenuActionPK> {
    List<MenuActionEntity> findAllByService(String service);
    List<MenuActionEntity> findAllByServiceAndPath(String service, String path);
    void deleteAllByService(String service);
    void deleteAllByServiceAndPath(String service, String path);

    List<MenuActionEntity> findAllByServiceAndOwnId(String service, String ownId);
    List<MenuActionEntity> findAllByServiceAndPathAndOwnId(String service, String path, String ownId);
    void deleteAllByServiceAndOwnId(String service, String ownId);
    void deleteAllByServiceAndPathAndOwnId(String service, String path, String ownId);
}
