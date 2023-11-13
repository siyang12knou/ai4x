package com.kailoslab.ai4x.commons.service;

import com.kailoslab.ai4x.commons.data.MenuActionRepository;
import com.kailoslab.ai4x.commons.data.MenuRepository;
import com.kailoslab.ai4x.commons.data.MenuUseRepository;
import com.kailoslab.ai4x.commons.data.entity.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class MenuService {

    private final MenuRepository menuRepository;
    private final MenuUseRepository menuUseRepository;
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
        menuUseRepository.deleteAllByServiceAndPath(service, path);
        menuActionRepository.deleteAllByServiceAndPath(service, path);
    }

    public List<MenuUseEntity> getMenuUseList(String service) {
        return menuUseRepository.findAllByService(service);
    }

    public List<MenuUseEntity> getMenuUseList(String service, String path) {
        return menuUseRepository.findAllByServiceAndPath(service, path);
    }

    public List<MenuUseEntity> getMenuUseListOfOwner(String service, String ownId) {
        return menuUseRepository.findAllByServiceAndOwnId(service, ownId);
    }

    public List<MenuUseEntity> getMenuUseListOfOwner(String service, String path, String ownId) {
        return menuUseRepository.findAllByServiceAndPathAndOwnId(service, path, ownId);
    }

    public List<MenuUseEntity> saveMenuUseList(List<MenuUseEntity> menuUseList) {
        return menuUseRepository.saveAll(menuUseList);
    }

    public MenuUseEntity saveMenuUse(MenuUseEntity menuUse) {
        return menuUseRepository.save(menuUse);
    }

    public void deleteMenuUseAndOwner(String service, String path, String ownId) {
        menuUseRepository.deleteAllByServiceAndPathAndOwnId(service, path, ownId);
    }

    public List<MenuActionEntity> getMenuActionList(String service) {
        return menuActionRepository.findAllByService(service);
    }

    public List<MenuActionEntity> getMenuActionList(String service, String path) {
        return menuActionRepository.findAllByServiceAndPath(service, path);
    }

    public List<MenuActionEntity> getMenuActionListOfOwner(String service, String ownId) {
        return menuActionRepository.findAllByServiceAndOwnId(service, ownId);
    }

    public List<MenuActionEntity> getMenuActionListOfOwner(String service, String path, String ownId) {
        return menuActionRepository.findAllByServiceAndPathAndOwnId(service, path, ownId);
    }

    public List<MenuActionEntity> saveMenuActionList(List<MenuActionEntity> menuActionList) {
        return menuActionRepository.saveAll(menuActionList);
    }

    public MenuActionEntity saveMenuAction(MenuActionEntity menuAction) {
        return menuActionRepository.save(menuAction);
    }

    public void deleteMenuActionAndOwner(String service, String path, String ownId) {
        menuActionRepository.deleteAllByServiceAndPathAndOwnId(service, path, ownId);
    }

    public void deleteMenuActionOfOwnerAndRole(String service, String path, String ownId, String roleId) {
        menuActionRepository.deleteById(new MenuActionPK(service, path, ownId, roleId));
    }

    public void clear(String service) {
        menuRepository.deleteAllByService(service);
        menuUseRepository.deleteAllByService(service);
        menuActionRepository.deleteAllByService(service);
    }
}
