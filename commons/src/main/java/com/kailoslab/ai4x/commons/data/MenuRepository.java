package com.kailoslab.ai4x.commons.data;

import com.kailoslab.ai4x.commons.data.entity.MenuEntity;
import com.kailoslab.ai4x.commons.data.entity.MenuPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<MenuEntity, MenuPK> {
    boolean existsByService(String service);
    List<MenuEntity> findAllByService(String service);
    void deleteAllByService(String service);
}
