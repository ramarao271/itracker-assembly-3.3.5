package org.itracker.persistence.dao;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.itracker.model.Language;

import java.util.List;

/**
 *
 */
public class LanguageDAOImpl extends BaseHibernateDAOImpl<Language>
        implements LanguageDAO {

    public Language findById(Integer id) {
        Language language;

        try {
            language = (Language) getSession().get(Language.class, id);
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return language;
    }

    public Language findByKeyAndLocale(String key, String locale) {
        Language language;

        try {
            Query query = getSession().getNamedQuery(
                    "LanguagesByKeyAndLocaleQuery");
            query.setString("key", key);
            query.setString("locale", locale);
            language = (Language) query.uniqueResult();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }

        if (language == null) {
            throw new NoSuchEntityException("No language item for "
                    + key + " " + locale);
        }
        return language;
    }

    @SuppressWarnings("unchecked")
    public List<Language> findByKey(String key) {
        List<Language> languages;

        try {
            Query query = getSession().getNamedQuery("LanguagesByKeyQuery");
            query.setString("key", key);
            languages = query.list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return languages;
    }

    @SuppressWarnings("unchecked")
    public List<Language> findByLocale(String locale) {
        List<Language> languages;

        try {
            Query query = getSession().getNamedQuery("LanguagesByLocaleQuery");
            query.setString("locale", locale);
            languages = query.list();
        } catch (HibernateException ex) {
            throw convertHibernateAccessException(ex);
        }
        return languages;
    }

}
