package org.itracker.services;

import org.apache.log4j.Logger;
import org.hibernate.ObjectNotFoundException;
import org.itracker.TestInitialContextFactory;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.*;
import org.itracker.model.CustomField.Type;
import org.itracker.model.util.CustomFieldUtilities;
import org.itracker.model.util.IssueUtilities;
import org.itracker.model.util.UserUtilities;
import org.itracker.persistence.dao.*;
import org.itracker.services.implementations.ConfigurationServiceImpl;
import org.junit.Test;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.itracker.Assert.*;

public class ConfigurationServiceIT extends
        AbstractServicesIntegrationTest {

    private static final Logger log = Logger
            .getLogger(ConfigurationServiceIT.class);

    private ConfigurationDAO configurationDAO;
    private ProjectDAO projectDAO;
    private ProjectScriptDAO projectScriptDAO;
    private WorkflowScriptDAO workflowScriptDAO;
    private CustomFieldDAO customFieldDAO;
    private CustomFieldValueDAO customFieldValueDAO;
    private LanguageDAO languageDAO;
    Properties configurationProperties;

    /**
     * Object to be Tested: configuration-service
     */
    ConfigurationService configurationService;


    @Test
    public void testConfigurationServiceImplConstructor() {
        try {
            configurationService = new ConfigurationServiceImpl(null, configurationDAO, customFieldDAO, customFieldValueDAO,
                    languageDAO, projectScriptDAO, workflowScriptDAO);
            fail("argument Properties configurationProperties is null, did not throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testLookupConfigurationItemById() {
        // defined in configurationbean_dataset.xml
        Configuration config = configurationService.getConfigurationItem(2000);

        assertNotNull("configuration (id:2000)", config);
        assertEquals("id", 2000, config.getId().intValue());
        DateFormat format = new SimpleDateFormat("M-d-y");
        try {
            assertEquals("create date", format.parse("1-1-2008"), config
                    .getCreateDate());
            assertEquals("modified date", format.parse("1-1-2008"), config
                    .getLastModifiedDate());
        } catch (ParseException e) {
            log
                    .error(
                            "testLookupConfigurationItemById: failed to parse date for assertion",
                            e);
            fail("failed to parse date for assertion: " + e.getMessage());
        }
        assertEquals("value", "Test Value", config.getValue());
        assertEquals("version", "Version 1.0", config.getVersion());
        assertEquals("order", 1, config.getOrder());
        assertEquals("type", (Object) 1, config.getType().getCode());
        assertEquals("type", Configuration.Type.locale, config.getType());

    }

    @Test
    public void testFailGetConfigurationItemById() {

        // not defined id
        Configuration config = configurationService
                .getConfigurationItem(999999);

        assertNull("non existing configuration item", config);

    }

    /**
     * Test if the configurationServiceImpl does override property values by JNDI.
     */
    @Test
    public void testGetJndiOverriddenProperty() throws Exception {
        final SimpleNamingContextBuilder namingContext = TestInitialContextFactory.getNamingContext();
        try {
            final String web_session_timeout = "web_session_timeout";
            namingContext.bind("java:comp/env/itracker/" + web_session_timeout, "300");


            Context ctx = new InitialContext();

            // basic assertions
            assertEquals("java:comp/env/itracker/web_session_timeout", "300", ctx.lookup("java:comp/env/itracker/" + web_session_timeout));

            String val = configurationProperties.getProperty(web_session_timeout);
            assertEquals("configurationProperties#web_session_timeout", "30", val);

            // check getProperty() in configurationService for jndi overridden value
            val = configurationService.getProperty(web_session_timeout);
            assertEquals("configurationService.web_session_timeout", "300", val);

        } finally {
            namingContext.deactivate();
        }
    }


    @Test
    public void testGetBooleanProperty() {
        boolean prop = configurationService.getBooleanProperty("non_existent_property_name", false);
        assertFalse(prop);

        prop = configurationService.getBooleanProperty("non_existent_property_name", true);
        assertTrue(prop);
    }

    @Test
    public void testGetIntegerProperty() {
        int prop = configurationService.getIntegerProperty("non_existent_property_name", 123456);
        assertEquals(123456, prop);

        prop = configurationService.getIntegerProperty("non_existent_property_name", 123456);
        assertEquals(123456, prop);

        //project property is in configuration.properties, value is itracker
        prop = configurationService.getIntegerProperty("project", 123456);
        assertEquals(123456, prop);
    }

    @Test
    public void testGetLongProperty() {
        long prop = configurationService.getLongProperty("non_existent_property_name", 123456L);
        assertEquals(123456, prop);

        prop = configurationService.getLongProperty("non_existent_property_name", 123456L);
        assertEquals(123456, prop);

        //project property is in configuration.properties, value is itracker
        prop = configurationService.getLongProperty("project", 123456);
        assertEquals(123456, prop);
    }


    @Test
    public void testProperty() {
        assertEquals("project property", configurationProperties
                .getProperty("default_locale"), configurationService
                .getProperty("default_locale"));

    }

    @Test
    public void testResetConfigurationCache() {
        //SystemConfigurationUtilities.TYPE_RESOLUTION, value 4
        configurationService.resetConfigurationCache(Configuration.Type.resolution);
        assertEquals(1, IssueUtilities.getResolutions(Locale.ENGLISH).size());

        //SystemConfigurationUtilities.TYPE_SEVERITY, value 3
        configurationService.resetConfigurationCache(Configuration.Type.severity);
        assertEquals(1, IssueUtilities.getSeverities(Locale.ENGLISH).size());

        //SystemConfigurationUtilities.TYPE_STATUS, value 2
        configurationService.resetConfigurationCache(Configuration.Type.status);
        assertEquals(1, IssueUtilities.getStatuses(Locale.ENGLISH).size());

        //SystemConfigurationUtilities.TYPE_CUSTOMFIELD, value 5
        configurationService.resetConfigurationCache(Configuration.Type.customfield);
        assertEquals(4, IssueUtilities.getCustomFields().size());
    }

    @Test
    public void testGetConfigurationItemsByType() {
        List<Configuration> configs = configurationService.getConfigurationItemsByType(Configuration.Type.locale);
        assertNotNull(configs);
        assertEquals("configs(locale)", 3, configs.size());
        assertEquals("configs(locale)[0].type", Configuration.Type.locale, configs.get(0).getType());

        configs = configurationService.getConfigurationItemsByType(Configuration.Type.status);
        assertNotNull(configs);
        assertEquals("configs(status)", 1, configs.size());
        assertEquals("configs(status)[0].type", Configuration.Type.status, configs.get(0).getType());

        configs = configurationService.getConfigurationItemsByType(Configuration.Type.severity);
        assertNotNull(configs);
        assertEquals("configs(severity)", 1, configs.size());
        assertEquals("configs(severity)[0].type", Configuration.Type.severity, configs.get(0).getType());

        configs = configurationService.getConfigurationItemsByType(Configuration.Type.resolution);
        assertNotNull(configs);
        assertEquals("configs(resolution)", 1, configs.size());
        assertEquals("configs(resolution)[0].type", Configuration.Type.resolution, configs.get(0).getType());


        configs = configurationService.getConfigurationItemsByType(Configuration.Type.locale, Locale.UK);
        assertNotNull(configs);
        assertEquals("configs(1, en_UK).size", 3, configs.size());

        configs = configurationService.getConfigurationItemsByType(Configuration.Type.status, Locale.UK);
        assertNotNull(configs);
        assertEquals("configs(2, en_UK).size", 1, configs.size());

        configs = configurationService.getConfigurationItemsByType(Configuration.Type.severity, Locale.UK);
        assertNotNull(configs);
        assertEquals("configs(3, en_UK).size", 1, configs.size());

        configs = configurationService.getConfigurationItemsByType(Configuration.Type.resolution, Locale.UK);
        assertNotNull(configs);
        assertEquals("configs(4, en_UK).size", 1, configs.size());


        // old checks
        configs = configurationService.getConfigurationItemsByType(2);
        assertNotNull(configs);
        assertEquals("configs(2)", 1, configs.size());
        assertEquals("configs(2)[0].type", Configuration.Type.status, configs.get(0).getType());

        //SystemConfigurationUtilities.TYPE_LOCALE, value is 1
        configs = configurationService.getConfigurationItemsByType(1, Locale.UK);
        assertNotNull(configs);
        assertEquals("configs(1, en_UK).size", 3, configs.size());

        //SystemConfigurationUtilities.TYPE_STATUS, value is 2
        configs = configurationService.getConfigurationItemsByType(2, Locale.UK);
        assertNotNull(configs);
        assertEquals("configs(2, en_UK).size", 1, configs.size());

        //SystemConfigurationUtilities.TYPE_SEVERITY, value is 3
        configs = configurationService.getConfigurationItemsByType(3, Locale.UK);
        assertNotNull(configs);
        assertEquals("configs(3, en_UK).size", 1, configs.size());

        //SystemConfigurationUtilities.TYPE_RESOLUTION, value is 4
        configs = configurationService.getConfigurationItemsByType(4, Locale.UK);
        assertNotNull(configs);
        assertEquals("configs(4, en_UK).size", 1, configs.size());

    }

    @Test
    public void testUpdateConfigurationItem() {
        Configuration conf = configurationDAO.findByPrimaryKey(2000);
        assertFalse(conf.getOrder() == 789);
        conf.setOrder(789);
        configurationService.updateConfigurationItem(conf);
        conf = configurationDAO.findByPrimaryKey(2000);
        assertEquals("new order value", 789, conf.getOrder());


    }

    @Test
    public void testUpdateConfigurationItems() {
        Configuration conf = configurationDAO.findByPrimaryKey(2000);
        assertFalse(conf.getOrder() == 987);
        conf.setOrder(987);
        List<Configuration> items = new ArrayList<Configuration>();
        items.add(conf);

        // FIXME: What's the purpose of passing type here?
        configurationService.updateConfigurationItems(items, Configuration.Type.locale);
        conf = configurationDAO.findByPrimaryKey(2000);
        assertEquals("new order value", 987, conf.getOrder());

    }

    @Test
    public void testRemoveConfigurationItem() {
        Configuration conf = new Configuration(Configuration.Type.status, "1", "1", 1);
        configurationDAO.save(conf);
        Integer id = conf.getId();
        assertNotNull(id);
        assertNotNull(configurationDAO.findByPrimaryKey(id));

        configurationService.removeConfigurationItem(id);
        conf = configurationDAO.findByPrimaryKey(id);
        assertNull("removed item", conf);

    }

    @Test
    public void testRemoveConfigurationItems() {

        Configuration conf = new Configuration(Configuration.Type.status, "1", "1", 123);
        configurationDAO.save(conf);
        Integer id = conf.getId();
        assertNotNull(id);
        assertNotNull(configurationDAO.findByPrimaryKey(id));

        configurationService.removeConfigurationItems(Configuration.Type.status);
        conf = configurationDAO.findByPrimaryKey(id);
        assertNull("removed item", conf);

        Configuration conf1 = new Configuration(Configuration.Type.status, "1", "1", 234);
        configurationDAO.save(conf1);
        Integer id1 = conf1.getId();
        assertNotNull(id1);
        assertNotNull(configurationDAO.findByPrimaryKey(id1));

        configurationService.removeConfigurationItems(conf1);
        conf1 = configurationDAO.findByPrimaryKey(id1);
        assertNull("removed item", conf1);
    }

    @Test
    public void testRemoveCustomFieldValues() {
        final Integer id = 4;
        CustomField customField = customFieldDAO.findByPrimaryKey(id);

        CustomFieldValue customFieldValue = new CustomFieldValue(
                customField, "my_value"
        );

        customFieldValue = configurationService.createCustomFieldValue(customFieldValue);
        assertNotNull(customFieldValue);
        assertNotNull(customFieldValue.getId());
        assertEquals("custom field value", "my_value", customFieldValue.getValue());

        assertNotNull("CustomField#" + id, customField);
        assertEquals("customField.size", 4, customField.getOptions().size());

        CustomFieldValue cfv = customField.getOptions().get(2);
        Integer cfvId = cfv.getId();
        assertNotNull("CustomFieldValue#id", cfvId);

        configurationService.removeCustomFieldValues(id);

        try {
            fail("delete #" + customFieldValueDAO.findByPrimaryKey(cfvId));
            customField = customFieldDAO.findByPrimaryKey(4);
            assertEquals("customField.size", 3, customField.getOptions().size());
        } catch (Exception e) {
            assertEquals(ObjectNotFoundException.class, e.getClass());
        }
    }

    @Test
    public void testConfigurationItemExists() {
        // searched by type and value ( + needs version!!)
        Configuration conf = new Configuration(Configuration.Type.locale, "Test Value", "1");
        assertTrue("conf type: 1, value: Test Value", configurationService.configurationItemExists(conf));

        conf = new Configuration(Configuration.Type.locale, "Unknown Value", "1");
        assertFalse("conf type: 1, value: Unknown Value", configurationService.configurationItemExists(conf));
    }

    @Test
    public void testConfigurationItemUpToDate() {

        Configuration configuration = new Configuration(Configuration.Type.locale, "Test Value", "Version 1.0", 1);
        assertTrue(configurationService.isConfigurationItemUpToDate(configuration));

    }

    @Test
    public void testGetProjectScript() {
        ProjectScript projectScript = configurationService.getProjectScript(1);
        assertNotNull("project script #1", projectScript);
        assertEquals("project script id", new Integer(1), projectScript.getId());
    }

    @Test
    public void testGetProjectScripts() {
        List<ProjectScript> projectScripts = configurationService.getProjectScripts();
        assertNotNull(projectScripts);
        assertEquals("total project scripts", 1, projectScripts.size());
    }

    @Test
    public void testCreateProjectScript() {
        ProjectScript projectScript = new ProjectScript();
        projectScript.setFieldId(1);
        projectScript.setFieldType(Configuration.Type.customfield);
        projectScript.setPriority(1);
        projectScript.setProject(projectDAO.findByPrimaryKey(3));
        projectScript.setScript(workflowScriptDAO.findByPrimaryKey(1));


        ProjectScript newProjectScript = configurationService.createProjectScript(projectScript);
        assertNotNull(newProjectScript);
        assertNotNull(newProjectScript.getId());
        assertEquals("priority", projectScript.getPriority(), newProjectScript.getPriority());
        assertEquals("script", projectScript.getScript(), newProjectScript.getScript());
        assertEquals("field id", projectScript.getFieldId(), newProjectScript.getFieldId());
        assertEquals("project", projectScript.getProject(), newProjectScript.getProject());

        // remove project script
        projectScriptDAO.delete(projectScript);
    }

    @Test
    public void testUpdateProjectScript() {
        ProjectScript projectScript = projectScriptDAO.findByPrimaryKey(1);
        projectScript.setPriority(999);

        ProjectScript newProjectScript = configurationService.updateProjectScript(projectScript);
        assertNotNull(newProjectScript);
        assertEquals("priority", 999, newProjectScript.getPriority());

        projectScript = projectScriptDAO.findByPrimaryKey(1);
        assertEquals("priority", 999, projectScript.getPriority());
    }

    @Test
    public void testRemoveProjectScript() {

        ProjectScript projectScript = new ProjectScript();
        projectScript.setFieldId(1);
        projectScript.setFieldType(Configuration.Type.customfield);
        projectScript.setPriority(1);
        projectScript.setProject(projectDAO.findByPrimaryKey(3));
        projectScript.setScript(workflowScriptDAO.findByPrimaryKey(1));
        projectScriptDAO.save(projectScript);
        Integer id = projectScript.getId();
        assertNotNull(id);
        configurationService.removeProjectScript(id);
        assertNull("removed project script", projectScriptDAO.findByPrimaryKey(id));
    }

    @Test
    public void testGetWorkflowScript() {
        WorkflowScript workflowScript = configurationService.getWorkflowScript(1);
        assertNotNull(workflowScript);
        assertEquals(new Integer(1), workflowScript.getId());
    }

    @Test
    public void testGetWorkflowScripts() {
        List<WorkflowScript> workflowScripts = configurationService.getWorkflowScripts();
        assertNotNull(workflowScripts);
        assertEquals("total worflow scripts", 2, workflowScripts.size());
    }

    @Test
    public void testCreateWorkflowScript() {
        WorkflowScript workflowScript = new WorkflowScript();
        workflowScript.setName("script");
        workflowScript.setScript("my_script");
        WorkflowScript newWorkflowScript = configurationService.createWorkflowScript(workflowScript);

        assertNotNull(newWorkflowScript);
        assertNotNull(newWorkflowScript.getId());
        assertEquals("new workflow script", "my_script", newWorkflowScript.getScript());
    }

    @Test
    public void testUpdateWorkflowScript() {
        WorkflowScript workflowScript = workflowScriptDAO.findByPrimaryKey(1);
        workflowScript.setScript("my_brand_new_script");
        configurationService.updateWorkflowScript(workflowScript);

        workflowScript = workflowScriptDAO.findByPrimaryKey(1);
        assertNotNull(workflowScript);
        assertEquals("new workflow script", "my_brand_new_script", workflowScript.getScript());
    }

    @Test
    public void testRemoveWorkflowScript() {
        WorkflowScript workflowScript = new WorkflowScript();
        workflowScript.setName("script");
        workflowScript.setScript("my_script");
        workflowScriptDAO.save(workflowScript);
        Integer id = workflowScript.getId();
        assertNotNull(id);

        configurationService.removeWorkflowScript(id);

        workflowScript = workflowScriptDAO.findByPrimaryKey(id);
        assertNull("removed script", workflowScript);

    }

    @Test
    public void testGetCustomField() {
        CustomField customField = configurationService.getCustomField(1);
        assertNotNull(customField);
        assertEquals(new Integer(1), customField.getId());
        assertTrue(customField.isRequired());
    }

    @Test
    public void testGetCustomFields() {
        List<CustomField> fields = configurationService.getCustomFields();
        assertNotNull("fields[]", fields);
        assertEquals("total custom fields", 4, fields.size());
    }

    @Test
    public void testCreateCustomField() {
        CustomField customField = new CustomField();
        customField.setFieldType(Type.STRING);
        customField = configurationService.createCustomField(customField);
        assertNotNull(customField);
        assertNotNull(customField.getId());
        assertNotNull(customField.getId());
        assertEquals("field type", Type.STRING, customField.getFieldType());
    }

    @Test
    public void testUpdateCustomField() {
        CustomField customField = customFieldDAO.findByPrimaryKey(1);
        assertTrue("required", customField.isRequired());
        customField.setRequired(false);
        configurationService.updateCustomField(customField);

        customField = customFieldDAO.findByPrimaryKey(1);
        assertFalse("required", customField.isRequired());

    }

    @Test
    public void testRemoveCustomField() throws Exception {
        //test CustomField which type is String
        CustomField customField = new CustomField();
        customField.setFieldType(Type.STRING);
        customFieldDAO.save(customField);
        Integer id = customField.getId();
        assertNotNull("id", id);
        assertNotNull("customFieldDAO.findByPrimaryKey(id)", customFieldDAO.findByPrimaryKey(id));

        configurationService.removeCustomField(id);
        assertNull("customFieldDAO.findByPrimaryKey(removed id)", customFieldDAO.findByPrimaryKey(id));


        //test CustomField which type is List
        CustomField customField1 = new CustomField();
        customField1.setFieldType(Type.LIST);
        customFieldDAO.save(customField1);
        Integer id1 = customField1.getId();
        assertNotNull("id1", id1);
        assertNotNull("customFieldDAO.findByPrimaryKey(id1)", customFieldDAO.findByPrimaryKey(id1));

        configurationService.removeCustomField(id1);
        assertNull("customFieldDAO.findByPrimaryKey(id1)", customFieldDAO.findByPrimaryKey(id1));
    }

    @Test
    public void testGetCustomFieldValue() {
        CustomFieldValue customFieldValue = configurationService.getCustomFieldValue(1);
        assertEquals("id", new Integer(1), customFieldValue.getId());
        assertEquals("value", "2", customFieldValue.getValue());

    }

    @Test
    public void testCreateCustomFieldValue() {
        CustomFieldValue customFieldValue = new CustomFieldValue(
                customFieldDAO.findByPrimaryKey(1), "my_value"
        );

        customFieldValue = configurationService.createCustomFieldValue(customFieldValue);
        assertNotNull("customFieldValue", customFieldValue);
        assertNotNull("customFieldValue.id", customFieldValue.getId());
        assertEquals("custom field value", "my_value", customFieldValue.getValue());

    }

    @Test
    public void testUpdateCustomFieldValue() {
        CustomFieldValue customFieldValue = customFieldValueDAO.findByPrimaryKey(1);
        assertEquals("value", "2", customFieldValue.getValue());

        customFieldValue.setValue("brand_new_value");
        configurationService.updateCustomFieldValue(customFieldValue);

        customFieldValue = customFieldValueDAO.findByPrimaryKey(1);
        assertEquals("new value", "brand_new_value", customFieldValue.getValue());

    }

    @Test
    public void testUpdateCustomFieldValues() {
        CustomFieldValue customFieldValue2 = customFieldValueDAO.findByPrimaryKey(2);
        CustomFieldValue customFieldValue3 = customFieldValueDAO.findByPrimaryKey(3);
        List<CustomFieldValue> customFieldValues = new ArrayList<CustomFieldValue>();
        customFieldValues.add(customFieldValue2);
        customFieldValues.add(customFieldValue3);

        assertEquals("value#2", "1", customFieldValue2.getValue());
        assertEquals("value#3", "4", customFieldValue3.getValue());

        customFieldValue2.setValue("22");
        customFieldValue3.setValue("33");

        configurationService.updateCustomFieldValues(1, customFieldValues);

        customFieldValue2 = customFieldValueDAO.findByPrimaryKey(2);
        customFieldValue3 = customFieldValueDAO.findByPrimaryKey(3);
        assertEquals("new value#2", "22", customFieldValue2.getValue());
        assertEquals("new value#3", "33", customFieldValue3.getValue());

    }

    @Test
    public void testRemoveCustomFieldValue() {
        CustomFieldValue customFieldValue = new CustomFieldValue(
                customFieldDAO.findByPrimaryKey(1), "my_value");
        customFieldValueDAO.save(customFieldValue);
        Integer id = customFieldValue.getId();
        assertNotNull("id", id);
        assertNotNull("customFieldValueDAO.findByPrimaryKey(id)", customFieldValueDAO.findByPrimaryKey(id));

        configurationService.removeCustomFieldValue(id);
        try {
            CustomFieldValue value = customFieldValueDAO.findByPrimaryKey(id);
            fail("custom field value: " + value);
        } catch (Exception e) {
            assertEquals(ObjectNotFoundException.class, e.getClass());
        }


    }

    @Test
    public void testGetLanguageItemByKey() {
        Language languageItem = configurationService.getLanguageItemByKey("test_key", null);
        assertNotNull("Language", languageItem);
        assertEquals("id", new Integer(9999971), languageItem.getId());
        assertEquals("resource_value", "test_value", languageItem.getResourceValue());

    }

    @Test
    public void testGetLanguageItemsByKey() {
        List<Language> languages = configurationService.getLanguageItemsByKey("test_key");
        assertNotNull(languages);
        assertEquals("total languaes with test_key key", 2, languages.size());

        languages = configurationService.getLanguageItemsByKey("non_existent_key");
        assertNotNull(languages);
        assertTrue("total languaes with test_key key", languages.isEmpty());
    }

    @Test
    public void testUpdateLanguageItem() {
        Language language = languageDAO.findById(999999);
        language.setLocale("de");
        configurationService.updateLanguageItem(language);
        language = languageDAO.findById(999999);
        assertEquals("locale", "de", language.getLocale());
    }

    @Test
    public void testRemoveLanguageKey() {
        Language language1 = new Language("ua", "key1", "value1");
        Language language2 = new Language("ru", "key1", "value2");
        languageDAO.save(language1);
        languageDAO.save(language2);
        assertNotNull(language1.getId());
        assertNotNull(language2.getId());

        configurationService.removeLanguageKey("key1");
        language1 = languageDAO.findById(language1.getId());
        language2 = languageDAO.findById(language2.getId());

        assertNull("removed language", language1);
        assertNull("removed language", language2);
    }

    @Test
    public void testRemoveLanguageItem() {
        Language language = new Language("ua", "my_key", "my_value");
        languageDAO.save(language);
        Integer id = language.getId();
        assertNotNull("id", id);

        configurationService.removeLanguageItem(language);
        language = languageDAO.findById(id);
        assertNull("removed language", language);
    }

    @Test
    public void testGetSortedKeys() {
        String[] keys = configurationService.getSortedKeys();
        assertNotNull("keys", keys);
        assertEquals("total keys", 2, keys.length); // search is done on a base locale
    }

    @Test

    public void testGetDefinedKeys() {
        Map<String, String> keyMap = configurationService.getDefinedKeys("test_locale");
        assertNotNull("keyMap", keyMap);
        assertEquals("total keys", 1, keyMap.size());

        keyMap = configurationService.getDefinedKeys("undefined_locale");
        assertNotNull("keyMap (undefined_locale)", keyMap);
        assertEquals("total keys", 0, keyMap.size());

    }

    @Test
    public void testGetDefinedKeysAsArray() {
        List<NameValuePair> keyMap = configurationService.getDefinedKeysAsArray("test_locale");
        assertNotNull("keyMap", keyMap);
        assertEquals("total keys", 1, keyMap.size());

        keyMap = configurationService.getDefinedKeysAsArray("undefined_locale");
        assertNotNull(keyMap);
        assertEquals("total keys", 0, keyMap.size());

    }

    @Test
    public void testGetNumberDefinedKeys() {
        int totalKeys = configurationService.getNumberDefinedKeys("test_locale");
        assertEquals("total keys", 1, totalKeys);

        totalKeys = configurationService.getNumberDefinedKeys("undefined_locale");
        assertEquals("total keys", 0, totalKeys);

    }

    @Test
    public void testGetAvailableLanguages() {
        Map<String, List<String>> availableLanguages = configurationService.getAvailableLanguages();
        assertNotNull(availableLanguages);

        // languagebean_dataset.xml contains one entry with 2-letter code
        assertEquals("available languages", 1, availableLanguages.size());

    }

    @Test
    public void testGetNumberAvailableLanguages() {
        int availableLanguages = configurationService.getNumberAvailableLanguages();
        assertEquals("available languages", 1, availableLanguages);

    }

    @Test
    public void testUpdateLanguage() {
        Language language = languageDAO.findById(999999);
        language.setResourceValue("brand_new_value");

        List<Language> items = new ArrayList<Language>();
        items.add(language);

        // FIXME: what's the purpose of passing locale here?
        configurationService.updateLanguage(Locale.ENGLISH, items);

        language = languageDAO.findById(999999);
        assertNotNull(language);
        assertEquals("resource value", "brand_new_value", language.getResourceValue());

    }


    @Test
    public void testGetLanguage() {
        List<Language> languages = configurationService.getLanguage(new Locale("test_locale"));
        assertNotNull(languages);
        assertEquals("total languages with test_locale", 1, languages.size());

        languages = configurationService.getLanguage(new Locale("undefined_locale"));
        assertNotNull(languages);
        assertEquals("total languages with undefined_locale", 0, languages.size());

    }

    private void doTestGetPermissionName(final Locale locale,
                                         final int permissionId, final String expected) {
        final String actual =
                UserUtilities.getPermissionName(permissionId, locale);
        assertEquals("UserUtilities.getPermissionName(" + permissionId + ", " +
                locale + ")", expected, actual);
    }

    @Test
    public void testInitializeConfiguration()
            throws Exception

    {

        doTestGetPermissionName(new Locale("test"),
                UserUtilities.PERMISSION_ASSIGN_OTHERS,
                "Assign Issues to Others");

        doTestGetPermissionName(new Locale("test"),
                UserUtilities.PERMISSION_ASSIGN_SELF,
                "Assign Issues to Self");
        doTestGetPermissionName(new Locale("test"),
                UserUtilities.PERMISSION_CLOSE, "Close Issues");

        doTestGetPermissionName(new Locale("test"),
                UserUtilities.PERMISSION_EDIT, "Edit All Issues");
        doTestGetPermissionName(new Locale("test"),
                UserUtilities.PERMISSION_CREATE, "Create Issues");

        doTestGetPermissionName(new Locale("test"),
                UserUtilities.PERMISSION_PRODUCT_ADMIN, "Project Admin");

        doTestGetPermissionName(new Locale("test"),
                UserUtilities.PERMISSION_USER_ADMIN, "test_value");
    }

    @Test
    public void testNameComparator() throws Exception {
        CustomFieldValue valueA, valueB;

        CustomField fieldA = new CustomField();
        CustomField fieldB = new CustomField();

        fieldA.setId(0);
        fieldB.setId(1);

        valueA = new CustomFieldValue(fieldA, "1");
        valueB = new CustomFieldValue(fieldB, "2");
        valueA.setId(1);
        valueB.setId(2);

        Language langA = new Language(ITrackerResources.getDefaultLocale(), CustomFieldUtilities.getCustomFieldOptionLabelKey(fieldA.getId(), valueA.getId()));
        langA.setResourceValue("a");

        Language langB = new Language(ITrackerResources.getDefaultLocale(), CustomFieldUtilities.getCustomFieldOptionLabelKey(fieldB.getId(), valueB.getId()));
        langB.setResourceValue("b");

        this.configurationService.updateLanguageItem(langA);
        this.configurationService.updateLanguageItem(langB);


        assertEntityComparator("name comparator",
                new CustomFieldUtilities.CustomFieldValueByNameComparator(null),
                valueA, valueB);
        assertEntityComparator("name comparator",
                new CustomFieldUtilities.CustomFieldValueByNameComparator(null),
                valueA, null);

        langA.setResourceValue(langB.getResourceValue());
        this.configurationService.updateLanguageItem(langA);
        this.configurationService.updateLanguageItem(langB);
        ITrackerResources.clearKeyFromBundles(langA.getResourceKey(), true);
        ITrackerResources.clearKeyFromBundles(langB.getResourceKey(), true);
//		Currently fails, the language item seems not to be initialized
//		assertEntityComparatorEquals("name comparator",
//				CustomFieldValue.NAME_COMPARATOR,
//				valueA, valueB);
        assertEntityComparatorEquals("name comparator",
                new CustomFieldUtilities.CustomFieldValueByNameComparator(null),
                valueA, valueA);
    }

    @Override
    public void onTearDown() throws Exception {
        super.onTearDown();
        //SystemConfigurationUtilities.initializeAllLanguages(configurationService, false);
        //configurationService.initializeConfiguration();
    }

    @Override
    public void onSetUp() throws Exception {

        super.onSetUp();
        configurationService = (ConfigurationService) applicationContext
                .getBean("configurationService");
        configurationProperties = (Properties) applicationContext
                .getBean("configurationProperties");

        this.configurationDAO = (ConfigurationDAO) applicationContext.getBean("configurationDAO");
        this.projectDAO = (ProjectDAO) applicationContext.getBean("projectDAO");
        this.projectScriptDAO = (ProjectScriptDAO) applicationContext.getBean("projectScriptDAO");
        this.workflowScriptDAO = (WorkflowScriptDAO) applicationContext.getBean("workflowScriptDAO");
        this.customFieldDAO = (CustomFieldDAO) applicationContext.getBean("customFieldDAO");
        this.customFieldValueDAO = (CustomFieldValueDAO) applicationContext.getBean("customFieldValueDAO");
        this.languageDAO = (LanguageDAO) applicationContext.getBean("languageDAO");


    }

    protected String[] getDataSetFiles() {
        return new String[]{
                "dataset/configurationbean_dataset.xml",
                "dataset/workflowscriptbean_dataset.xml",
                "dataset/projectbean_dataset.xml",
                "dataset/projectscriptbean_dataset.xml",
                "dataset/languagebean_dataset.xml",
                "dataset/customfieldbean_dataset.xml",
                "dataset/customfieldvaluebean_dataset.xml",

        };
    }

}
