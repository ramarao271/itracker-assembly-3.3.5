package org.itracker.persistence.dao;

import org.hibernate.SessionFactory;
import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.model.Language;
import org.junit.Test;
import org.springframework.dao.DataAccessException;
import static org.itracker.Assert.*;

/**
 * Unit test for BaseHibernateDaoImpl
 *
 * @author rdjurin
 */
public class BaseHibernateDaoImplIT extends AbstractDependencyInjectionTest {


    BaseHibernateDAOImpl<Language> dao;
    LanguageDAO languageDAO;

    @Test
    public void testSave() {

        try {
            dao.save(null);
            fail("save of null entity must throw DataAccessException");
        } catch (DataAccessException dae) {
            // OK
        } catch (Throwable t) {
            fail("save of null entity must throw DataAccessException, not " + t);
        }

        Language entity = new Language();
        entity.setLocale("en_US");
        entity.setResourceKey("org.itracker.test.BaseHibernateDAOImpl");

        try {
            dao.save(null);
            fail("save of null not-null property must throw DataAccessException");
        } catch (DataAccessException dae) {
            // OK
        } catch (Throwable t) {
            fail("save of null not-null property must throw DataAccessException, not "
                    + t);
        }

        entity.setResourceValue("BaseHibernateDAOImpl");
        assertTrue("entity.new", entity.isNew());
        dao.save(entity);
        assertTrue("entity.!new", !entity.isNew());
        try {
            Language l = languageDAO.findByKeyAndLocale(
                    entity.getResourceKey(), entity.getLocale());

            assertEquals("entity", entity, l);
            // TODO: fails currently
            // try {
            // dao.save(l);
            // fail("entity is already persisted, save must throw DataAccessException");
            // } catch (DataAccessException dae) {
            // // OK
            // }

        } catch (DataAccessException dae) {
            fail("failed to load entity from languageDAO");
        }

    }

    @Test
    public void testSaveOrUpdate() {

        try {
            dao.saveOrUpdate(null);
            fail("saveOrUpdate of null entity must throw DataAccessException");
        } catch (DataAccessException dae) {
            // OK
        } catch (Throwable t) {
            fail("saveOrUpdate of null entity must throw DataAccessException, not "
                    + t);
        }

        Language entity = new Language();
        entity.setLocale("en_US");
        entity.setResourceKey("org.itracker.test.BaseHibernateDAOImpl");

        try {
            dao.saveOrUpdate(null);
            fail("saveOrUpdate of null not-null property must throw DataAccessException");
        } catch (DataAccessException dae) {
            // OK
        } catch (Throwable t) {
            fail("saveOrUpdate of null not-null property must throw DataAccessException, not "
                    + t);
        }

        entity.setResourceValue("BaseHibernateDAOImpl");
        dao.saveOrUpdate(entity);

        try {
            Language l = languageDAO.findByKeyAndLocale(
                    entity.getResourceKey(), entity.getLocale());

            assertEquals("entity", entity, l);
            try {
                dao.saveOrUpdate(entity);
            } catch (Throwable t) {
                fail("saveOrUpdate of persisted entity must not throw " + t);
            }
        } catch (DataAccessException dae) {
            fail("failed to load entity from languageDAO");
        }
    }

    @Test
    public void testDelete() throws Exception {

        try {
            dao.delete(null);
            fail("delete of null entity must throw DataAccessException");
        } catch (DataAccessException dae) {
            // OK
        }

        Language entity = new Language();
        entity.setLocale("en_US");
        entity.setResourceKey("org.itracker.test.BaseHibernateDAOImpl");
        entity.setResourceValue("BaseHibernateDAOImpl");

        dao.save(entity);

        entity = languageDAO.findByKeyAndLocale(entity.getResourceKey(), entity
                .getLocale());
        dao.delete(entity);

        try {
            entity = languageDAO.findByKeyAndLocale(entity.getResourceKey(),
                    entity.getLocale());
            fail("expecting to catch NoSuchEntityException");
        } catch (NoSuchEntityException dae) {
            // OK
        }
    }

    @Test
    public void testDetach() throws Exception {
        try {
            dao.detach(null);
            fail("detach of null entity must throw DataAccessException");
        } catch (DataAccessException dae) {
            // OK
        }

        Language entity = new Language();
        entity.setLocale("en_US");
        entity.setResourceKey("org.itracker.test.BaseHibernateDAOImpl");
        entity.setResourceValue("BaseHibernateDAOImpl");

        dao.save(entity);

        entity = languageDAO.findByKeyAndLocale(entity.getResourceKey(), entity
                .getLocale());
        dao.detach(entity);

        Language l = languageDAO.findByKeyAndLocale(entity.getResourceKey(),
                entity.getLocale());
        assertNotSame("detached object", l, entity);

    }

    @Test
    public void testRefresh() throws Exception {

        try {
            dao.refresh(null);
            fail("Should throw DataAccessException");
        } catch (DataAccessException e) {
            // OK
        } catch (Exception e) {
            fail("Should throw DataAccessException not " + e);
        }

        Language entity = new Language();
        entity.setLocale("en_US");
        entity.setResourceKey("org.itracker.test.BaseHibernateDAOImpl");
        entity.setResourceValue("BaseHibernateDAOImpl");

        dao.save(entity);
        dao.detach(entity);

        Language l = languageDAO.findByKeyAndLocale(entity.getResourceKey(),
                entity.getLocale());
        dao.detach(l);

        entity.setResourceValue("org.itracker.test.BaseHibernateDAO2");
        dao.merge(entity);
        assertNotSame("detached and managed entity are same", l, entity);

        assertTrue("entity.resourceValue not equals l.resourceValue", !entity
                .getResourceValue().equals(l.getResourceValue()));

        l = languageDAO.findByKeyAndLocale(entity.getResourceKey(), entity
                .getLocale());
        assertEquals("entity.resourceValue", l.getResourceValue(), entity
                .getResourceValue());

    }

    @Test
    public void testMerge() throws Exception {
        try {
            dao.merge(null);
            fail("Should throw DataAccessException");
        } catch (DataAccessException e) {
            // OK
        } catch (Exception e) {
            fail("Should throw DataAccessException not " + e);
        }

        Language entity = new Language();
        entity.setLocale("en_US");
        entity.setResourceKey("org.itracker.test.BaseHibernateDAOImpl");
        entity.setResourceValue("BaseHibernateDAOImpl");

        dao.save(entity);
        dao.detach(entity);

        Language l = languageDAO.findByKeyAndLocale(entity.getResourceKey(),
                entity.getLocale());
        l.setResourceValue("test");
        dao.saveOrUpdate(l);

        entity.setResourceValue("org.itracker.test.BaseHibernateDAOImpl2");

        assertTrue("entity.resourceValue not equals l.resourceValue", !l
                .getResourceValue().equals(entity.getResourceValue()));

        dao.detach(l);
        dao.merge(entity);

        assertTrue("entity.resourceValue not equals l.resourceValue", !l
                .getResourceValue().equals(entity.getResourceValue()));

        l = languageDAO.findByKeyAndLocale(entity.getResourceKey(), entity
                .getLocale());

        assertEquals("entity.resourceKey", l.getResourceKey(), entity
                .getResourceKey());
        assertEquals("entity.resourceValue", l.getResourceValue(), entity
                .getResourceValue());
    }

    @Override
    public void onSetUp() throws Exception {
        super.onSetUp();

        languageDAO = (LanguageDAO) applicationContext.getBean("languageDAO");
        dao = new BaseHibernateDAOImpl<Language>() {
        };
        dao.setSessionFactory((SessionFactory) applicationContext
                .getBean("sessionFactory"));
    }

    @Override
    protected String[] getDataSetFiles() {
        return new String[]{"dataset/languagebean_dataset.xml"};
    }
}
