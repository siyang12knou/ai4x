package com.kailoslab.ai4x.commons.service;

import com.kailoslab.ai4x.commons.annotation.CodeGroup;
import com.kailoslab.ai4x.commons.annotation.Title;
import com.kailoslab.ai4x.commons.data.CodeRepository;
import com.kailoslab.ai4x.commons.data.entity.CodeEntity;
import com.kailoslab.ai4x.commons.data.entity.CodePK;
import com.kailoslab.ai4x.commons.utils.Constants;
import com.kailoslab.ai4x.commons.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

import static org.reflections.scanners.Scanners.TypesAnnotated;

@RequiredArgsConstructor
@Service
public class CodeService implements ApplicationListener<ApplicationStartedEvent> {

    private final ApplicationContext applicationContext;
    private final CodeRepository codeRepository;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        List<String> scanPackages = Utils.getScanPackages(applicationContext);
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

    public List<CodeEntity> getCodeList(String groupId) {
        return codeRepository.findByGroupIdAndDeletedFalseOrderByOrdinal(groupId);
    }

    public void saveCode(CodeGroup codeGroup, Class<?> codeGroupClass) {
        String codeGroupId = StringUtils.isNotEmpty(codeGroup.value()) ? codeGroup.value() : Utils.toFirstLowerCase(codeGroupClass.getSimpleName());
        CodePK pk = new CodePK(Constants.DEFAULT_GROUP_ID, codeGroupId);
        CodeEntity codeGroupEntity = codeRepository.findById(pk).orElse(new CodeEntity(pk));
        codeGroupEntity.setName(getTitle(codeGroupClass));
        saveCode(codeGroupEntity);
        for(Object code: codeGroupClass.getEnumConstants()) {
            String id = Utils.getString(code, "name");
            String name = getTitle(codeGroupClass, code);
            if(!StringUtils.isAnyEmpty(id, name)) {
                int ordinal = Utils.getInt(code, "ordinal");
                pk = new CodePK(codeGroupId, id);
                CodeEntity codeEntity = codeRepository.findById(pk).orElse(new CodeEntity(pk));
                codeEntity.setName(name);
                codeEntity.setOrdinal(ordinal);
                saveCode(codeEntity);
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
            String codeId = Utils.getString(code, "name");
            Field field = codeGroupClass.getField(codeId);
            Title title = field.getAnnotation(Title.class);
            return title == null || StringUtils.isEmpty(title.value()) ?
                    Utils.getString(code, "getName", "name") : title.value();
        } catch (NoSuchFieldException e) {
            return Utils.getString(code, "getName", "name");
        }
    }
}
