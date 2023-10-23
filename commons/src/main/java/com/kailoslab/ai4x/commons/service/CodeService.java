package com.kailoslab.ai4x.commons.service;

import com.kailoslab.ai4x.commons.annotation.CodeGroup;
import com.kailoslab.ai4x.commons.annotation.DefaultCode;
import com.kailoslab.ai4x.commons.annotation.Title;
import com.kailoslab.ai4x.commons.data.CodeRepository;
import com.kailoslab.ai4x.commons.data.dto.CodeDto;
import com.kailoslab.ai4x.commons.data.entity.CodeEntity;
import com.kailoslab.ai4x.commons.data.entity.CodePK;
import com.kailoslab.ai4x.utils.Ai4xUtils;
import com.kailoslab.ai4x.utils.Constants;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;

import static org.reflections.scanners.Scanners.TypesAnnotated;

@RequiredArgsConstructor
@Service
public class CodeService implements ApplicationListener<ApplicationStartedEvent> {

    private final ApplicationContext applicationContext;
    private final CodeRepository codeRepository;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        List<String> scanPackages = Ai4xUtils.getScanPackages(applicationContext);
        scanPackages.forEach(scanPackage -> {
            Reflections reflections = new Reflections(scanPackage,
                    TypesAnnotated);
            Set<Class<?>> classSet = reflections.getTypesAnnotatedWith(CodeGroup.class);
            classSet.forEach(clazz -> {
                if(clazz.isEnum()) {
                    CodeGroup codeGroup = clazz.getDeclaredAnnotation(CodeGroup.class);
                    saveCode(codeGroup, clazz);
                }
            });
        });
    }

    public Map<String, List<CodeEntity>> getCodeAll() {
        List<CodeEntity> groupCodeList = getCodeGroupList();
        Map<String, List<CodeEntity>> codeList = new HashMap<>(groupCodeList.size());
        for(CodeEntity groupCode: groupCodeList) {
            codeList.put(groupCode.getCodeId(), getCodeList(groupCode.getCodeId()));
        }

        return codeList;
    }

    public List<CodeEntity> getCodeGroupList() {
        return getCodeList(Constants.DEFAULT_GROUP_ID);
    }

    public List<CodeEntity> getCodeList(String groupId) {
        return codeRepository.findByGroupIdAndDeletedFalseOrderByOrdinal(groupId);
    }

    public void saveCode(CodeGroup codeGroup, Class<?> codeGroupClass) {
        String codeGroupId = StringUtils.isNotEmpty(codeGroup.value()) ? codeGroup.value() : Ai4xUtils.toFirstLowerCase(codeGroupClass.getSimpleName());
        CodePK pk = new CodePK(Constants.DEFAULT_GROUP_ID, codeGroupId);
        if(!codeRepository.existsById(pk)) {
            CodeEntity codeGroupEntity = codeRepository.findById(pk).orElse(new CodeEntity(pk));
            codeGroupEntity.setName(getTitle(codeGroupClass));
            saveCode(codeGroupEntity);
            for (Object code : codeGroupClass.getEnumConstants()) {
                String id = Ai4xUtils.getString(code, "name");
                String name = getTitle(codeGroupClass, code);
                if (!StringUtils.isAnyEmpty(id, name)) {
                    int ordinal = Ai4xUtils.getInt(code, "ordinal");
                    pk = new CodePK(codeGroupId, id);
                    CodeEntity codeEntity = codeRepository.findById(pk).orElse(new CodeEntity(pk));
                    codeEntity.setName(name);
                    codeEntity.setOrdinal(ordinal);
                    saveCode(codeEntity);
                }
            }
        }
    }

    public void saveCode(CodeEntity codeEntity) {
        codeRepository.save(codeEntity);
    }

    private String getTitle(Class<?> codeGroupClass) {
        Title title = codeGroupClass.getAnnotation(Title.class);
        return title == null || StringUtils.isEmpty(title.value()) ?
                codeGroupClass.getSimpleName() : title.value();
    }

    private String getTitle(Class<?> codeGroupClass, Object code) {
        try {
            String codeId = Ai4xUtils.getString(code, "name");
            Field field = codeGroupClass.getField(codeId);
            Title title = field.getAnnotation(Title.class);
            return title == null || StringUtils.isEmpty(title.value()) ?
                    Ai4xUtils.getString(code, "getName", "name") : title.value();
        } catch (NoSuchFieldException e) {
            return Ai4xUtils.getString(code, "getName", "name");
        }
    }

    private Boolean isDefaultCode(Class<?> codeGroupClass, Object code) {
        try {
            String codeId = Ai4xUtils.getString(code, "name");
            Field field = codeGroupClass.getField(codeId);
            DefaultCode title = field.getAnnotation(DefaultCode.class);
            return title != null;
        } catch (NoSuchFieldException e) {
            return false;
        }
    }

    public List<CodeDto> getCodeListFromCodeGroupEnum(Class<? extends Enum> codeGroupClass) {
        return getCodeListFromCodeGroupEnum(codeGroupClass, null);
    }

    public List<CodeDto> getCodeListFromCodeGroupEnum(Class<? extends Enum> codeGroupClass, List<String> includeCodeId) {
        CodeGroup codeGroup = codeGroupClass.getDeclaredAnnotation(CodeGroup.class);
        String codeGroupId = StringUtils.isNotEmpty(codeGroup.value()) ? codeGroup.value() : Ai4xUtils.toFirstLowerCase(codeGroupClass.getSimpleName());
        Object[] codeList = codeGroupClass.getEnumConstants();
        List<CodeDto> result = new ArrayList<>(codeList.length);
        for(Object code: codeList) {
            String id = Ai4xUtils.getString(code, "name");
            if(includeCodeId == null || includeCodeId.contains(id)) {
                String name = getTitle(codeGroupClass, code);
                Boolean defaultCode = isDefaultCode(codeGroupClass, code);
                if (!StringUtils.isAnyEmpty(id, name)) {
                    result.add(new CodeDto(id, name, defaultCode));
                }
            }
        }

        return result;
    }
}
