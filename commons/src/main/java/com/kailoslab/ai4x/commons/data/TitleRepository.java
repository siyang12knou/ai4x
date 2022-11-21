package com.kailoslab.ai4x.commons.data;

import com.kailoslab.ai4x.commons.data.entity.TitleEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Locale;

public interface TitleRepository  extends JpaRepository<TitleEntity, String> {
    default String generateId(String titleKey) {
        return generateId(titleKey, Locale.getDefault());
    }

    default String generateId(String titleKey, Locale locale) {
        return generateId(titleKey, locale.getLanguage(), locale.getCountry());
    }

    default String generateId(String titleKey, String language, String country) {
        return "%s_%s.%s".formatted(StringUtils.trimToEmpty(language), StringUtils.trimToEmpty(country), StringUtils.trimToEmpty(titleKey));
    }

    List<TitleEntity> findAllByIdStartingWith(String id);
    List<TitleEntity> findAllByTitleKey(String titleKey);
    List<TitleEntity> findAllByCountry(String country);
}
