package com.kailoslab.ai4x.commons.service;

import com.kailoslab.ai4x.commons.data.MenuActionRepository;
import com.kailoslab.ai4x.commons.data.MenuRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;
    private final MenuActionRepository menuActionRepository;


}
