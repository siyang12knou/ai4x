package com.kailoslab.ai4x.commons.data;

import com.kailoslab.ai4x.commons.data.entity.MenuUseEntity;
import com.kailoslab.ai4x.commons.data.entity.MenuUsePK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuUseRepository extends JpaRepository<MenuUseEntity, MenuUsePK> {
    List<MenuUseEntity> findAllByService(String service);
    List<MenuUseEntity> findAllByServiceAndPath(String service, String path);
    void deleteAllByService(String service);
    void deleteAllByServiceAndPath(String service, String path);

    List<MenuUseEntity> findAllByServiceAndOwnId(String service, String ownId);
    List<MenuUseEntity> findAllByServiceAndPathAndOwnId(String service, String path, String ownId);
    void deleteAllByServiceAndOwnId(String service, String ownId);
    void deleteAllByServiceAndPathAndOwnId(String service, String path, String ownId);
}
