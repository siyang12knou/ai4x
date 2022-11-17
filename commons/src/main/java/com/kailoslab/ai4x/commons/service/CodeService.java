package com.kailoslab.ai4x.commons.service;

import com.kailoslab.ai4x.commons.data.CodeRepository;
import com.kailoslab.ai4x.commons.data.entity.CodeEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CodeService {
    private final CodeRepository codeRepository;

    public List<CodeEntity> getCodeList(String groupId) {
        return codeRepository.findByGroupIdAndDeletedFalseOrderByOrdinal(groupId);
    }
}
