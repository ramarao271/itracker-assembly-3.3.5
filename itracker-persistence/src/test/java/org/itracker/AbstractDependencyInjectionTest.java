package org.itracker;

import org.apache.log4j.Logger;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.CompositeDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.XmlDataSet;
import org.dbunit.ext.hsqldb.HsqldbDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.core.resources.ITrackerResourcesProvider;
import org.itracker.model.Language;
import org.itracker.persistence.dao.LanguageDAO;
import org.itracker.util.NamingUtilites;
import org.junit.After;
import org.junit.Before;
import org.springframework.orm.hibernate3.SessionHolder;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;

@ContextConfiguration("/application-context.xml")
public abstract class AbstractDependencyInjectionTest extends
        org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests {

    private static final Logger log = Logger
            .getLogger(AbstractDependencyInjectionSpringContextTests.class);
    private DataSource dataSource;
    public ClassLoader classLoader;
    public IDataSet dataSet;
    private SessionFactory sessionFactory;
    private HashSet<String> definedLocales;

    protected AbstractDependencyInjectionTest() {
        classLoader = getClass().getClassLoader();
    }


    @Before
    public void initDatabase() throws Exception {
        log.debug("initDatabase");
        log.info("setting up " + getClass());
        sessionFactory = (SessionFactory) applicationContext
                .getBean("sessionFactory");
        Session session = sessionFactory.openSession();
        TransactionSynchronizationManager.bindResource(sessionFactory,
                new SessionHolder(session));

        dataSet = getDataSet();
        DatabaseConnection dbConnection = null;
        try {

            resetConfiguration();

            dbConnection = new DatabaseConnection(getDataSource().getConnection());
            dbConnection.getConfig().setProperty(
                    DatabaseConfig.PROPERTY_DATATYPE_FACTORY,
                    new HsqldbDataTypeFactory());

            if (dataSet != null) {
                DatabaseOperation.CLEAN_INSERT.execute(dbConnection, dataSet);
            }

            if (!dbConnection.getConnection().getAutoCommit()) {
                dbConnection.getConnection().commit();
            }
        } catch (Exception e) {
            log.error("onSetUp: failed to set up datasets", e);
            throw e;
        } finally {
            if (null != dbConnection) {
                try {
                    dbConnection.close();
                } catch (SQLException e) {
                    log.warn("onSetUp: failed to close connection", e);
                }
            }
        }
        onSetUp();
    }


    @After
    public void closeDatabase() throws Exception {
        onTearDown();
        log.debug("closeDatabase");
        DatabaseConnection dbConnection = null;
        try {
            dbConnection = new DatabaseConnection(getDataSource()
                    .getConnection());

            if (dataSet != null) {
                DatabaseOperation.DELETE_ALL.execute(dbConnection, dataSet);
            }

            if (!dbConnection.getConnection().getAutoCommit()) {
                dbConnection.getConnection().commit();
            }

        } catch (Exception e) {
            log.error("onTearDown: failed to tear down datasets", e);
            throw e;
        } finally {
            TransactionSynchronizationManager.unbindResource(sessionFactory);

            if (null != dbConnection) {
                try {
                    dbConnection.close();
                } catch (SQLException e) {
                    log.warn("onTearDown: failed to close connection", e);
                }
            }
        }
    }

    private Properties getLanguageProperties(String locale) {
        final LanguageDAO dao = (LanguageDAO) applicationContext.getBean("languageDAO");
        final List<Language> languages = dao.findByLocale(locale);
        final Properties properties = new Properties();
        for (Language language : languages) {
            properties.setProperty(language.getResourceKey(), language.getResourceValue());
        }
        return properties;
    }

    @Deprecated
    public void onSetUp() throws Exception {
        // this does nothing
        log.debug("onSetUp called " + getClass());
    }

    @Deprecated
    public void onTearDown() throws Exception {
        // this does nothing
        log.debug("onTearDown called " + getClass());
    }

    public String getProperty(String name) {
        String value = null;
        final String jndiPropertiesOverridePrefix = getConfigurationProperties().getProperty("jndi_override_prefix", "java:comp/env/itracker");
        if (null != jndiPropertiesOverridePrefix) {

            if (logger.isDebugEnabled()) {

                logger.debug("getProperty: looking up '" + name
                        + "' from jndi context "
                        + jndiPropertiesOverridePrefix);


            }
            try {
                value = (String) NamingUtilites.lookup(new InitialContext(),
                        jndiPropertiesOverridePrefix + "/" + name);
                if (null == value) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("getProperty: value not found in jndi: " + name);
                    }
                }
            } catch (Exception e) {
                logger.debug("getProperty: caught exception looking up value for " + name, e);
            }

        }

        if (null == value) {
            value = getConfigurationProperties().getProperty(name, null);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("getProperty: returning " + value + " for name: " + name);
        }
        return value;
    }


    private Set<String> getDefinedLocales() {
        final HashSet<String> definedLocales = new HashSet<>();

        String definedLocalesString;

        definedLocalesString = getConfigurationProperties().getProperty("available_locales", ITrackerResources.getDefaultLocale());

        if (definedLocalesString != null) {
            StringTokenizer token = new StringTokenizer(definedLocalesString, ",");
            while (token.hasMoreTokens()) {
                String locale = token.nextToken();
                if (locale.length() == 5 && locale.indexOf('_') == 2) {
                    definedLocales.add(locale.substring(0, 2));
                }
                definedLocales.add(locale);
            }
        }
        return definedLocales;
    }

    public void initializeLocale(String locale) {
        ITrackerResources.clearBundle(ITrackerResources.getLocale(locale));
    }

    public void initializeAllLanguages() {
        initializeLocale(ITrackerResources.BASE_LOCALE);
        Set<String> definedLocales = getDefinedLocales();


        for (String locale : definedLocales) {
            initializeLocale(locale);
        }
    }

    private Properties getConfigurationProperties() {
        return (Properties) applicationContext.getBean("configurationProperties");

    }

    protected void resetConfiguration() {

        synchronized (ITrackerResources.class) {
            if (ITrackerResources.isInitialized()) {
                return;
            }
            final Hashtable<Locale, Properties> properties = new Hashtable<>(getDefinedLocales().size());
            for (String locale : getDefinedLocales()) {
                final Properties p = getLanguageProperties(locale);
                properties.put(ITrackerResources.getLocale(locale), p);
            }

            ITrackerResources.setConfigurationService(new ITrackerResourcesProvider() {
                @Override
                public Properties getLanguageProperties(Locale locale) {
                    if (!properties.contains(locale)) {
                        properties.put(locale, AbstractDependencyInjectionTest
                                .this.getLanguageProperties(locale.toString()));
                    }
                    return properties.get(locale);
                }

                @Override
                public String getLanguageEntry(String key, Locale locale) {

                    return getLanguageProperties(locale).getProperty(key);
                }

                @Override
                public String getProperty(String name, String defaultValue) {
                    return getConfigurationProperties().getProperty(name, defaultValue);
                }
            });
            initializeAllLanguages();

        }
    }

    private IDataSet getDataSet() throws Exception {
        final String[] aDataSet = getDataSetFiles();
        final IDataSet[] dataSets = new IDataSet[aDataSet.length];

        for (int i = 0; i < aDataSet.length; i++) {
            dataSets[i] = new XmlDataSet(classLoader
                    .getResourceAsStream(aDataSet[i]));
        }
        return new CompositeDataSet(dataSets);
    }

    /**
     * must make sure, that the order is correct, so no constraints will be
     * violated.
     */
    protected abstract String[] getDataSetFiles();

    public DataSource getDataSource() {
        if (null == this.dataSource) {
            this.dataSource = (DataSource) applicationContext.getBean("dataSource");
        }
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }


}
