package org.itracker.persistence.dao;

import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.model.Language;
import org.junit.Test;

import java.util.List;

import static org.itracker.Assert.*;
public class LanguageDAOImplIT extends AbstractDependencyInjectionTest {

    private LanguageDAO languageDAO;

    @Test
    public void testFindByID() {

        Language language = languageDAO.findById(999999);

        assertNotNull(language);
        assertLanguageEquals(language, "test_locale", "test_key", "test_value");

    }

    @Test
    public void testFindByKeyAndLocale() {

        Language language = languageDAO.findByKeyAndLocale(
                "test_key", "test_locale");

        assertNotNull(language);

        assertLanguageEquals(language, "test_locale",
                "test_key", "test_value");

    }

    @Test
    public void testFindByKey() {

        List<Language> languages = languageDAO.findByKey("test_key");

        assertNotNull(languages);

        assertEquals(2, languages.size());

        Language language = languages.get(0);

        assertLanguageEquals(language, "test_locale",
                "test_key", "test_value");

    }

    @Test
    public void testFindByLocale() {

        List<Language> languages = languageDAO.findByLocale("test_locale");

        assertNotNull(languages);

        assertEquals(1, languages.size());

        Language language = languages.get(0);

        assertLanguageEquals(language, "test_locale",
                "test_key", "test_value");

    }

    private void assertLanguageEquals(Language language,
                                      String locale,
                                      String key,
                                      String value) {

        assertEquals(locale, language.getLocale());
        assertEquals(key, language.getResourceKey());
        assertEquals(value, language.getResourceValue());

    }

    @Override
    public void onSetUp() throws Exception {
        super.onSetUp();

        languageDAO = (LanguageDAO) applicationContext.getBean("languageDAO");
    }

    protected String[] getDataSetFiles() {
        return new String[]{
                "dataset/languagebean_dataset.xml"
        };
    }


}
