package com.kailoslab.ai4x.commons.data;

import com.kailoslab.ai4x.commons.data.entity.MenuEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<MenuEntity, String> {
}
