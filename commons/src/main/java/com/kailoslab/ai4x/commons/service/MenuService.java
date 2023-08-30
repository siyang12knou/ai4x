package com.kailoslab.ai4x.commons.service;

import com.kailoslab.ai4x.commons.data.MenuActionRepository;
import com.kailoslab.ai4x.commons.data.MenuRepository;
import com.kailoslab.ai4x.commons.data.entity.MenuActionEntity;
import com.kailoslab.ai4x.commons.data.entity.MenuActionPK;
import com.kailoslab.ai4x.commons.data.entity.MenuEntity;
import com.kailoslab.ai4x.commons.data.entity.MenuPK;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class MenuService {

    private final MenuRepository menuRepository;
    private final MenuActionRepository menuActionRepository;

    public boolean isEmpty(String service) {
        return !menuRepository.existsByService(service);
    }

    public List<MenuEntity> saveMenuList(List<MenuEntity> menuList) {
        return menuRepository.saveAll(menuList);
    }

    public MenuEntity saveMenu(MenuEntity menu) {
        return menuRepository.save(menu);
    }

    public List<MenuEntity> getMenuList(String service) {
        return menuRepository.findAllByService(service);
    }

    public MenuEntity getMenu(String service, String path) {
        return menuRepository.findById(new MenuPK(service, path)).orElse(null);
    }

    public void deleteMenu(String service, String path) {
        menuRepository.deleteById(new MenuPK(service, path));
        menuActionRepository.deleteAllByServiceAndPath(service, path);
    }

    public List<MenuActionEntity> getMenuActionList(String service) {
        return menuActionRepository.findAllByService(service);
    }

    public List<MenuActionEntity> getMenuActionList(String service, String path) {
        return menuActionRepository.findAllByServiceAndPath(service, path);
    }

    public List<MenuActionEntity> saveMenuActionList(List<MenuActionEntity> menuActionList) {
        return menuActionRepository.saveAll(menuActionList);
    }

    public MenuActionEntity saveMenuAction(MenuActionEntity menuAction) {
        return menuActionRepository.save(menuAction);
    }

    public void deleteMenuAction(String service, String path, String roleId) {
        menuActionRepository.deleteById(new MenuActionPK(service, path, roleId));
    }

    public void clear(String service) {
        menuRepository.deleteAllByService(service);
        menuActionRepository.deleteAllByService(service);
    }
}
