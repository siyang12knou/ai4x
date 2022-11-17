package com.kailoslab.ai4x.commons;

import com.kailoslab.ai4x.commons.data.entity.CodeEntity;
import com.kailoslab.ai4x.commons.service.CodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RequiredArgsConstructor
@Controller
public class CodeController {
    private final CodeService codeService;

    @GetMapping(path="/code/{groupId}")
    public List<CodeEntity> getCodeList(@PathVariable("groupId") String groupId) {
        return codeService.getCodeList(groupId);
    }
}
