package com.kailoslab.ai4x.commons.controller;

import com.kailoslab.ai4x.commons.data.entity.CodeEntity;
import com.kailoslab.ai4x.commons.service.CodeService;
import com.kailoslab.ai4x.utils.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = Constants.PATH_API_AI4X_PREFIX + "/code")
public class CodeController {
    private final CodeService codeService;

    @GetMapping(path="/{groupId}")
    public List<CodeEntity> getCodeList(@PathVariable("groupId") String groupId) {
        return codeService.getCodeList(groupId);
    }
}
