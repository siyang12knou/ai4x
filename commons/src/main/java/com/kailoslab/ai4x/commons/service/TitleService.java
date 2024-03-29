package com.kailoslab.ai4x.commons.service;

import com.kailoslab.ai4x.commons.annotation.Title;
import com.kailoslab.ai4x.commons.data.TitleRepository;
import com.kailoslab.ai4x.commons.data.entity.TitleEntity;
import com.kailoslab.ai4x.utils.Ai4xUtils;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import static org.reflections.scanners.Scanners.*;

@RequiredArgsConstructor
@Service
public class TitleService implements ApplicationListener<ApplicationStartedEvent> {

    private final ApplicationContext applicationContext;
    private final TitleRepository titleRepository;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        List<String> scanPackages = Ai4xUtils.getScanPackages(applicationContext);
        scanPackages.forEach(scanPackage -> {
            Reflections reflections = new Reflections(scanPackage,
                    TypesAnnotated, FieldsAnnotated, MethodsAnnotated );
            Set<Class<?>> classSet = reflections.getTypesAnnotatedWith(Title.class);
            classSet.forEach(clazz -> {
                Title title = clazz.getDeclaredAnnotation(Title.class);
                saveTitle(title, Ai4xUtils.toFirstLowerCase(clazz.getSimpleName()));
            });

            Set<Field> fieldSet = reflections.getFieldsAnnotatedWith(Title.class);
            fieldSet.forEach(field -> {
                Title title = field.getDeclaredAnnotation(Title.class);
                Class<?> type = field.getType();
                saveTitle(title, Ai4xUtils.toFirstLowerCase(type.getSimpleName()) + "." + Ai4xUtils.toFirstLowerCase(field.getName()));
            });

            Set<Method> methodSet = reflections.getMethodsAnnotatedWith(Title.class);
            methodSet.forEach(method -> {
                Title title = method.getDeclaredAnnotation(Title.class);
                Class<?> type = method.getDeclaringClass();
                saveTitle(title, Ai4xUtils.toFirstLowerCase(type.getSimpleName()) + "." + Ai4xUtils.toFirstLowerCase(method.getName()));
            });
        });
    }

    private void saveTitle(Title title, String defaultTitleKey) {
        if(title != null && StringUtils.isNotEmpty(title.value())) {
            String titleKey = StringUtils.defaultIfEmpty(title.titleKey(), defaultTitleKey);
            String id = titleRepository.generateId(titleKey);
            TitleEntity titleEntity = titleRepository.findById(id).orElse(new TitleEntity(id));
            titleEntity.setTitleKey(titleKey);
            titleEntity.setLang(Locale.getDefault().getLanguage());
            titleEntity.setCountry(Locale.getDefault().getCountry());
            titleEntity.setTitle(title.value());
            titleRepository.save(titleEntity);
        }
    }

    private void save(TitleEntity titleEntity) {
        titleRepository.save(titleEntity);
    }

    public List<TitleEntity> getTitles(String language, String country) {
        if(StringUtils.isNoneEmpty(language, country)) {
            if(StringUtils.isEmpty(language)) {
                return titleRepository.findAllByCountry(country);
            } else {
                return titleRepository.findAllByIdStartingWith(language + (StringUtils.isNotEmpty(country) ? "_" + country : ""));
            }
        } else {
            return new ArrayList<>(titleRepository.findAll());
        }
    }

    public List<TitleEntity> getTitles(String titleKey) {
        if(StringUtils.isEmpty(titleKey)) {
            return titleRepository.findAllByTitleKey(titleKey);
        } else {
            return Collections.emptyList();
        }
    }

    public String getTitle(Title title) {
        String value = getTitle(title.titleKey());
        return StringUtils.isNotEmpty(value) ? value : title.value();
    }

    public String getTitle(String titleKey) {
        return getTitle(titleKey, Locale.getDefault().getLanguage(), Locale.getDefault().getCountry());
    }

    public String getTitle(String titleKey, String language, String country) {
        if(StringUtils.isNoneEmpty(titleKey, language, country)) {
            Optional<TitleEntity> titleEntityOptional = titleRepository.findById(titleRepository.generateId(titleKey, language, country));
            if (titleEntityOptional.isPresent()) {
                return titleEntityOptional.get().getTitle();
            }
        }

        return "";
    }

    public String getTitle(Title title, Class<?> clazz) {
        String titleKey = StringUtils.defaultIfEmpty(title.titleKey(), Ai4xUtils.toFirstLowerCase(clazz.getSimpleName()));
        return getTitle(titleKey);
    }

    public String getTitle(Title title, Field field) {
        Class<?> type = field.getType();
        String titleKey = StringUtils.defaultIfEmpty(title.titleKey(),
                Ai4xUtils.toFirstLowerCase(type.getSimpleName()) + "." + Ai4xUtils.toFirstLowerCase(field.getName()));
        return getTitle(titleKey);
    }

    public String getTitle(Title title, Method method) {
        Class<?> type = method.getDeclaringClass();
        String titleKey = StringUtils.defaultIfEmpty(title.titleKey(),
                Ai4xUtils.toFirstLowerCase(type.getSimpleName()) + "." + Ai4xUtils.toFirstLowerCase(method.getName()));
        return getTitle(titleKey);
    }
}
