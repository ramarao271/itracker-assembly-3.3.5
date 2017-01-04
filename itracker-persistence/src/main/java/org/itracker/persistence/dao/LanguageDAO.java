package org.itracker.persistence.dao;

import org.itracker.model.Language;

import java.util.List;

public interface LanguageDAO extends BaseDAO<Language> {

    Language findById(Integer id);

    /**
     * Finds all language items with the given key and locale.
     *
     * @param key resource key
     * @return language items for the given locale
     */
    Language findByKeyAndLocale(String key, String locale);

    /**
     * Finds all language items with a particular key.
     *
     * @param key resource key
     * @return language items with the given key for all available locales
     */
    List<Language> findByKey(String key);

    /**
     * Finds all language items with a given locale.
     *
     * @return language items for the given locale
     */
    List<Language> findByLocale(String locale);

}
