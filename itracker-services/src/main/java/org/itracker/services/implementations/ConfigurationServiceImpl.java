/*
/*
 * This software was designed and created by Jason Carroll.
 * Copyright (c) 2002, 2003, 2004 Jason Carroll.
 * The author can be reached at jcarroll@cowsultants.com
 * ITracker website: http://www.cowsultants.com
 * ITracker forums: http://www.cowsultants.com/phpBB/index.php
 *
 * This program is free software; you can redistribute it and/or modify
 * it only under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package org.itracker.services.implementations;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.NotNullPredicate;
import org.apache.commons.lang.StringUtils;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.*;
import org.itracker.model.util.CustomFieldUtilities;
import org.itracker.model.util.IssueUtilities;
import org.itracker.model.util.SystemConfigurationUtilities;
import org.itracker.persistence.dao.*;
import org.itracker.services.ConfigurationService;
import org.itracker.util.NamingUtilites;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.InitialContext;
import java.util.*;

/**
 * Implementation of the ConfigurationService Interface.
 *
 * @see ConfigurationService
 */

public class ConfigurationServiceImpl implements ConfigurationService {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationServiceImpl.class.getName());

    private final Properties props;
    private ConfigurationDAO configurationDAO;
    private CustomFieldDAO customFieldDAO;
    private CustomFieldValueDAO customFieldValueDAO;
    private LanguageDAO languageDAO;
    private ProjectScriptDAO projectScriptDAO;
    private WorkflowScriptDAO workflowScriptDAO;


    private static final Long _START_TIME_MILLIS = System.currentTimeMillis();
    private String jndiPropertiesOverridePrefix;
    private String mailSessionLookupName;

    /**
     * Creates a new instance using the given configuration.
     *
     * @param configurationProperties itracker configuration properties
     *                                (see classpath:configuration.properties)
     */
    public ConfigurationServiceImpl(Properties configurationProperties,
                                    ConfigurationDAO configurationDAO, CustomFieldDAO customFieldDAO,
                                    CustomFieldValueDAO customFieldValueDAO, LanguageDAO languageDAO,
                                    ProjectScriptDAO projectScriptDAO, WorkflowScriptDAO workflowScriptDAO) {
        if (configurationProperties == null) {
            throw new IllegalArgumentException("null configurationProperties");
        }
        this.props = configurationProperties;
        props.setProperty("start_time_millis", String.valueOf(_START_TIME_MILLIS));

        // initialize naming context prefix for properties overrides
        if (StringUtils.isEmpty(jndiPropertiesOverridePrefix)) {
            jndiPropertiesOverridePrefix = props.getProperty("jndi_override_prefix", "java:comp/env/itracker");
        }
        if (StringUtils.isEmpty(mailSessionLookupName)) {
            mailSessionLookupName = configurationProperties.getProperty("mail_session_jndi_lookup", "java:comp/env/itracker/mail/Session");
        }

        this.configurationDAO = configurationDAO;
        this.customFieldDAO = customFieldDAO;
        this.customFieldValueDAO = customFieldValueDAO;
        this.languageDAO = languageDAO;

        this.projectScriptDAO = projectScriptDAO;
        this.workflowScriptDAO = workflowScriptDAO;
    }

    public String getJndiPropertiesOverridePrefix() {
        return jndiPropertiesOverridePrefix;
    }

    public void setJndiPropertiesOverridePrefix(String jndiPropertiesOverridePrefix) {
        if (null != jndiPropertiesOverridePrefix) {
            return;
        }
        this.jndiPropertiesOverridePrefix = jndiPropertiesOverridePrefix;
    }

    public String getMailSessionLookupName() {
        return mailSessionLookupName;
    }

    public void setMailSessionLookupName(String mailSessionLookupName) {
        this.mailSessionLookupName = mailSessionLookupName;
    }

    public String getProperty(String name) {
        String value = null;
        if (null != getJndiPropertiesOverridePrefix()) {

            if (logger.isDebugEnabled()) {

                logger.debug("getProperty: looking up '" + name
                        + "' from jndi context "
                        + getJndiPropertiesOverridePrefix());


            }
            try {
                value = NamingUtilites.getStringValue(new InitialContext(),
                        getJndiPropertiesOverridePrefix() + "/" + name, null);
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
            value = props.getProperty(name, null);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("getProperty: returning " + value + " for name: " + name);
        }
        return value;
    }

    public String getProperty(String name, String defaultValue) {
        String val = getProperty(name);
        return (val == null) ? defaultValue : val;
    }

    private String getItrackerVersion() {
        return props.getProperty("version");
    }


    public boolean getBooleanProperty(String name, boolean defaultValue) {
        String value = getProperty(name);

        return (value == null ? defaultValue : Boolean.valueOf(value));
    }

    public int getIntegerProperty(String name, int defaultValue) {
        String value = getProperty(name);

        try {
            return (value == null) ? defaultValue : Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return defaultValue;
        }

    }

    public long getLongProperty(String name, long defaultValue) {
        String value = getProperty(name);
        try {
            return (value == null) ? defaultValue : Long.parseLong(value);
        } catch (NumberFormatException ex) {
            return defaultValue;
        }

    }

    public Configuration getConfigurationItem(Integer id) {
        Configuration configItem = configurationDAO.findByPrimaryKey(id);
        return configItem;
    }

    @Deprecated
    public List<Configuration> getConfigurationItemsByType(int type) {
        return getConfigurationItemsByType(Configuration.Type.valueOf(type));
    }

    public List<Configuration> getConfigurationItemsByType(Configuration.Type type) {
        List<Configuration> configItems = configurationDAO.findByType(type);
        Collections.sort(configItems, new Configuration.ConfigurationOrderComparator());
        return configItems;
    }

    @Deprecated
    public List<Configuration> getConfigurationItemsByType(int type, Locale locale) {
        return getConfigurationItemsByType(Configuration.Type.valueOf(type), locale);
    }

    public List<Configuration> getConfigurationItemsByType(Configuration.Type type, Locale locale) {
        List<Configuration> items = getConfigurationItemsByType(type);

        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getType() == Configuration.Type.status) {
                items.get(i).setName(IssueUtilities.getStatusName(items.get(i).getValue(), locale));
            } else if (items.get(i).getType() == Configuration.Type.severity) {
                items.get(i).setName(IssueUtilities.getSeverityName(items.get(i).getValue(), locale));
            } else if (items.get(i).getType() == Configuration.Type.resolution) {
                items.get(i).setName(IssueUtilities.getResolutionName(items.get(i).getValue(), locale));
            }
        }
        return items;
    }

    public Configuration createConfigurationItem(Configuration configuration) {

        Configuration configurationItem = new Configuration();

        configurationItem.setType(configuration.getType());
        configurationItem.setOrder(configuration.getOrder());
        configurationItem.setValue(configuration.getValue());
        configurationItem.setCreateDate(new Date());
        configurationItem.setVersion(getItrackerVersion());
        configurationDAO.saveOrUpdate(configurationItem);

        return configurationItem;

    }


    public Configuration updateConfigurationItem(Configuration configuration) {
        // find item by primary key
        Configuration configurationItem = configurationDAO.findByPrimaryKey(configuration.getId());

        configurationItem.setVersion(getInitializedVersionString());

        // update now
        configurationDAO.saveOrUpdate(configurationItem);
        // get model from saved item
        return configurationItem;
    }

    @Deprecated
    public List<Configuration> updateConfigurationItems(List<Configuration> configurations, Configuration.Type type) {

        List<Configuration> configurationItems = new ArrayList<Configuration>();
        for (Configuration configurationItem : configurations) {
            if (type == configurationItem.getType()) {
                // create a new item
                Configuration curConfiguration = configurationDAO.findByPrimaryKey(configurationItem.getId());

                curConfiguration.setName(configurationItem.getName());
                curConfiguration.setOrder(configurationItem.getOrder());
                curConfiguration.setType(configurationItem.getType());
                curConfiguration.setValue(configurationItem.getValue());
                curConfiguration.setVersion(getInitializedVersionString());

                // save or update
                this.configurationDAO.saveOrUpdate(curConfiguration);
                configurationItems.add(curConfiguration);
            }
        }
        // sort array
        Collections.sort(configurationItems);

        return configurationItems;
    }

    /**
     * Finds the <code>Configuration</code> by primary key <code>id<code>
     * and deletes it.
     *
     * @param id The id of the <code>COnfigurationBean</code> to remove
     */
    public void removeConfigurationItem(Integer id) {

        Configuration configBean = this.configurationDAO.findByPrimaryKey(id);
        if (configBean != null) {
            this.configurationDAO.delete(configBean);
        }
    }

    /**
     * Removes all <code>Configuration</code>s of the give <code>type</code>
     *
     * @param type the type of <code>Configuration</code> to remove
     * @deprecated
     */
    public void removeConfigurationItems(int type) {
        removeConfigurationItems(Configuration.Type.valueOf(type));
    }

    /**
     * Removes all <code>Configuration</code>s of the give <code>type</code>
     *
     * @param type the type of <code>Configuration</code> to remove
     */
    public void removeConfigurationItems(Configuration.Type type) {

        // find the configuration beans by its type
        Collection<Configuration> currentItems = configurationDAO.findByType(type);

        for (Iterator<Configuration> iter = currentItems.iterator(); iter.hasNext(); ) {
            // get current config bean
            Configuration config = (Configuration) iter.next();
            // delete it
            this.configurationDAO.delete(config);
        }
    }

    public void removeConfigurationItems(Configuration configuration) {
        Collection<Configuration> currentItems = configurationDAO.findByTypeAndValue(configuration.getType(), configuration.getValue());
        for (Iterator<Configuration> iter = currentItems.iterator(); iter.hasNext(); ) {
            Configuration configItem = (Configuration) iter.next();
            configurationDAO.delete(configItem);
        }
    }

    public boolean configurationItemExists(Configuration configuration) {

        if (configuration != null && configuration.getVersion() != null) {

            Collection<Configuration> configItems = configurationDAO.findByTypeAndValue(configuration.getType(), configuration.getValue());

            if (configItems != null && configItems.size() > 0) {

                return true;

            }

        }

        return false;

    }

    public String getInitializedVersionString() {
        List<Configuration> initialized = getConfigurationItemsByType(Configuration.Type.initialized);
        if (null == initialized || initialized.isEmpty()) {
            return "0";
        }
        Collections.sort(initialized, new Comparator<Configuration>() {
            public int compare(Configuration configuration, Configuration configuration1) {
                return configuration.getVersion().compareTo(configuration1.getVersion());
            }
        });

        return initialized.get(initialized.size() - 1).getVersion();
    }

    public boolean isConfigurationItemUpToDate(Configuration configuration) {

        if (null == configuration) {
            return false;
        }

        if (StringUtils.endsWith(configuration.getVersion(), "-SNAPSHOT")) {
            return false;
        }

        long currentVersion = SystemConfigurationUtilities.getVersionAsLong(getInitializedVersionString());

        if (configuration != null && configuration.getVersion() != null) {

            Collection<Configuration> configItems = configurationDAO.findByTypeAndValue(configuration.getType(), configuration.getValue());

            for (Iterator<Configuration> iter = configItems.iterator(); iter.hasNext(); ) {

                Configuration configItem = (Configuration) iter.next();

                if (null != configItem) {

                    currentVersion = Math.max(SystemConfigurationUtilities.getVersionAsLong(configItem.getVersion()),
                            currentVersion);

                }

            }

            if (currentVersion >= SystemConfigurationUtilities.getVersionAsLong(configuration.getVersion())) {

                return true;

            }

        }

        return false;

    }

    public void resetConfigurationCache() {

        IssueUtilities.setResolutions(getConfigurationItemsByType(Configuration.Type.resolution));
        IssueUtilities.setSeverities(getConfigurationItemsByType(Configuration.Type.severity));
        IssueUtilities.setStatuses(getConfigurationItemsByType(Configuration.Type.status));
        IssueUtilities.setCustomFields(getCustomFields());

    }

    public void resetConfigurationCache(Configuration.Type type) {
        switch (type) {
            case resolution:
                IssueUtilities.setResolutions(getConfigurationItemsByType(type));
                break;
            case severity:
                IssueUtilities.setSeverities(getConfigurationItemsByType(Configuration.Type.severity));
                break;
            case status:
                IssueUtilities.setStatuses(getConfigurationItemsByType(Configuration.Type.status));
                break;
            case customfield:
                IssueUtilities.setCustomFields(getCustomFields());
                break;
            default:
                logger.warn("resetConfigurationCache: unsupported type " + type);

        }
    }

    @Deprecated
    public void resetConfigurationCache(int type) {
        logger.warn("resetConfigurationCache: called with deprecated API!");
        resetConfigurationCache(Configuration.Type.valueOf(type));

    }

    public ProjectScript getProjectScript(Integer scriptId) {
        ProjectScript projectScript = this.projectScriptDAO.findByPrimaryKey(scriptId);
        return projectScript;

    }

    public List<ProjectScript> getProjectScripts() {
        List<ProjectScript> projectScripts = this.projectScriptDAO.findAll();
        return projectScripts;
    }


    public ProjectScript createProjectScript(ProjectScript projectScript) {

        // create project script and populate data
        ProjectScript editprojectScript = new ProjectScript();
        editprojectScript.setFieldId(projectScript.getFieldId());
        editprojectScript.setFieldType(projectScript.getFieldType());
        editprojectScript.setPriority(projectScript.getPriority());
        editprojectScript.setProject(projectScript.getProject());
        editprojectScript.setScript(projectScript.getScript());

        // save entity
        this.projectScriptDAO.save(editprojectScript);

        return editprojectScript;
    }

    public ProjectScript updateProjectScript(ProjectScript projectScript) {
        ProjectScript editprojectScript;

        editprojectScript = projectScriptDAO.findByPrimaryKey(projectScript.getId());
        editprojectScript.setFieldId(projectScript.getFieldId());
        editprojectScript.setFieldType(projectScript.getFieldType());
        editprojectScript.setPriority(projectScript.getPriority());
        editprojectScript.setProject(projectScript.getProject());
        editprojectScript.setScript(projectScript.getScript());
        this.projectScriptDAO.saveOrUpdate(editprojectScript);
        return editprojectScript;
    }

    /**
     * remove a project script by its id
     *
     * @param projectScript_id the id of the project script to remove
     */
    public void removeProjectScript(Integer projectScript_id) {
        if (projectScript_id != null) {
            ProjectScript projectScript = this.projectScriptDAO.findByPrimaryKey(projectScript_id);
            if (projectScript != null) {
                this.projectScriptDAO.delete(projectScript);
            }
        }
    }

    public WorkflowScript getWorkflowScript(Integer id) {

        WorkflowScript workflowScript = workflowScriptDAO.findByPrimaryKey(id);

        return workflowScript;

    }

    public List<WorkflowScript> getWorkflowScripts() {
        List<WorkflowScript> workflowScripts = workflowScriptDAO.findAll();
        return workflowScripts;
    }

    /**
     * Creates a workflow script.
     *
     * @param workflowScript The <code>WorkflowScript</code> carring the data
     * @return The <code>WorkflowScript</code> after inserting
     */
    public WorkflowScript createWorkflowScript(WorkflowScript workflowScript) {

        // create workflow script and populate data
        WorkflowScript editworkflowScript = new WorkflowScript();
        editworkflowScript.setName(workflowScript.getName());
        editworkflowScript.setScript(workflowScript.getScript());
        editworkflowScript.setEvent(workflowScript.getEvent());
        editworkflowScript.setLanguage(workflowScript.getLanguage());
        // save entity
        workflowScriptDAO.save(editworkflowScript);

        return editworkflowScript;
    }

    public WorkflowScript updateWorkflowScript(WorkflowScript workflowScript) {
        WorkflowScript editworkflowScript;

        editworkflowScript = workflowScriptDAO.findByPrimaryKey(workflowScript.getId());
        editworkflowScript.setName(workflowScript.getName());
        editworkflowScript.setScript(workflowScript.getScript());
        editworkflowScript.setEvent(workflowScript.getEvent());
        editworkflowScript.setLanguage(workflowScript.getLanguage());
        workflowScriptDAO.saveOrUpdate(editworkflowScript);
        return editworkflowScript;
    }

    /**
     * remove a workflow script by its id
     *
     * @param workflowScript_id the id of the workflow script to remove
     */
    public void removeWorkflowScript(Integer workflowScript_id) {
        if (workflowScript_id != null) {
            WorkflowScript workflowScript = this.workflowScriptDAO.findByPrimaryKey(workflowScript_id);
            if (workflowScript != null) {
                this.workflowScriptDAO.delete(workflowScript);
            }
        }
    }

    public CustomField getCustomField(Integer id) {

        CustomField customField = customFieldDAO.findByPrimaryKey(id);

        return customField;

    }

    @Deprecated
    public List<CustomField> getCustomFields() {
        return getCustomFieldsSorted(null);
    }

    public List<CustomField> getCustomFieldsSorted(Locale locale) {
        List<CustomField> customFields = customFieldDAO.findAll();
        Collections.sort(customFields, new CustomFieldUtilities.CustomFieldByNameComparator((null == locale)
                ? ITrackerResources.getLocale(ITrackerResources.getDefaultLocale())
                : locale));
        return customFields;
    }

    /**
     * Creates a custom field
     *
     * @param customField The <code>CustomField</code> carrying the data
     * @return the <code>CustomField</code> after saving
     */
    public CustomField createCustomField(CustomField customField) {
        CustomField addcustomField = new CustomField();
        addcustomField.setDateFormat(customField.getDateFormat());
        addcustomField.setFieldType(customField.getFieldType());
        addcustomField.setOptions(customField.getOptions());
        addcustomField.setRequired(customField.isRequired());
        this.customFieldDAO.save(addcustomField);

        return addcustomField;
    }

    public CustomField updateCustomField(CustomField customField) {
        CustomField editcustomField = customFieldDAO.findByPrimaryKey(customField.getId());

        editcustomField.setDateFormat(customField.getDateFormat());
        editcustomField.setFieldType(customField.getFieldType());
        editcustomField.setOptions(customField.getOptions());
        editcustomField.setRequired(customField.isRequired());
        this.customFieldDAO.saveOrUpdate(editcustomField);

        return editcustomField;
    }

    /**
     * searches for a custom field by primary key and removes it
     *
     * @param customFieldId the primary key
     */
    public boolean removeCustomField(Integer customFieldId) {
        boolean status = true;
        boolean del_Status = true;
        CustomField customField = customFieldDAO.findByPrimaryKey(customFieldId);

        if (customField != null) {
            try {
                if (customField.getFieldType() == CustomField.Type.LIST)
                    status = this.removeCustomFieldValues(customFieldId);
                String key = CustomFieldUtilities.getCustomFieldLabelKey(customField.getId());
                this.customFieldDAO.delete(customField);
                if (key != null)
                    status = this.removeLanguageKey(key);
            } catch (Exception ex) {
                del_Status = false;
            }
        }
        if (!del_Status)
            status = del_Status;

        return status;
    }


    /**
     * Gets a <code>CustomFieldValue</code> by primary key
     *
     * @param id the primary key
     * @return The <code>CustomFieldValue</code> found or <code>null</code>
     */
    public CustomFieldValue getCustomFieldValue(Integer id) {

        CustomFieldValue cfvBean = (CustomFieldValue)
                this.customFieldValueDAO.findByPrimaryKey(id);

        return cfvBean;
    }

    public CustomFieldValue createCustomFieldValue(CustomFieldValue customFieldValue) {
        CustomFieldValue addcustomFieldValue = new CustomFieldValue();
        addcustomFieldValue.setCustomField(customFieldValue.getCustomField());
        addcustomFieldValue.setValue(customFieldValue.getValue());
        this.customFieldValueDAO.save(addcustomFieldValue);

        return addcustomFieldValue;
    }


    /**
     * Updates a <code>CustomFieldValue</code>.
     *
     * @param customFieldValue The model to update
     * @return The <code>CustomFieldValue</code> after saving
     */
    public CustomFieldValue updateCustomFieldValue(CustomFieldValue customFieldValue) {
        CustomFieldValue editcustomFieldValue = this.customFieldValueDAO.findByPrimaryKey(customFieldValue.getId());
        editcustomFieldValue.setCustomField(customFieldValue.getCustomField());
        editcustomFieldValue.setValue(customFieldValue.getValue());
        this.customFieldValueDAO.saveOrUpdate(editcustomFieldValue);

        return editcustomFieldValue;
    }

    public List<CustomFieldValue> updateCustomFieldValues(Integer customFieldId, List<CustomFieldValue> customFieldValues) {
        List<CustomFieldValue> customFieldValueItems = new ArrayList<CustomFieldValue>();

        if (customFieldId != null) {
            try {
                CustomField customField = customFieldDAO.findByPrimaryKey(customFieldId);
                if (customFieldValues != null && !customFieldValues.isEmpty()) {
                    for (Iterator<CustomFieldValue> iterator = customFieldValues.iterator(); iterator.hasNext(); ) {

                        // create a new item
                        CustomFieldValue customFieldValueItem = (CustomFieldValue) iterator.next();
                        CustomFieldValue curCustomFieldValue = customFieldValueDAO.findByPrimaryKey(customFieldValueItem.getId());

                        curCustomFieldValue.setCreateDate(customFieldValueItem.getCreateDate());
                        curCustomFieldValue.setValue(customFieldValueItem.getValue());
                        curCustomFieldValue.setCustomField(customFieldValueItem.getCustomField());
                        curCustomFieldValue.setSortOrder(customFieldValueItem.getSortOrder());

                        // save or update
                        this.customFieldValueDAO.saveOrUpdate(curCustomFieldValue);
                        customFieldValueItems.add(curCustomFieldValue);

                    }
                    customField.setOptions(customFieldValueItems);
                    return customFieldValueItems;

                }
            } catch (Exception fe) {
            }
        }

        return customFieldValues;
    }

    /**
     * removes a custom field value by primary key
     *
     * @param customFieldValueId the id of the custoem field
     */
    public boolean removeCustomFieldValue(Integer customFieldValueId) {
        boolean status = true;
        boolean del_Status = true;

        // find custom field value by id
        CustomFieldValue customFieldValue = this.customFieldValueDAO.findByPrimaryKey(customFieldValueId);

        // delete it
        try {
            this.customFieldValueDAO.delete(customFieldValue);
        } catch (Exception ex) {
            del_Status = false;
        }
        if (!del_Status)
            status = del_Status;

        return status;
    }

    /**
     * Removes all field values of a given custom field
     *
     * @param customFieldId The id of the customField
     */
    public boolean removeCustomFieldValues(Integer customFieldId) {
        boolean status = true;
        boolean lp_Status = true;
        CustomField customField = this.customFieldDAO.findByPrimaryKey(customFieldId);
        // get values of the field
        List<CustomFieldValue> customFieldValues = customField.getOptions();
        for (Iterator<CustomFieldValue> iter = customFieldValues.iterator(); iter.hasNext(); ) {
            // get current
            CustomFieldValue customFieldValue = (CustomFieldValue) iter.next();
            String key = CustomFieldUtilities.getCustomFieldOptionLabelKey(customFieldId, customFieldValue.getId());
            // remove from collection
            iter.remove();
            // delete from datasource
            try {
                this.customFieldValueDAO.delete(customFieldValue);

                if (key != null)
                    status = this.removeLanguageKey(key);
            } catch (Exception ex) {
                lp_Status = false;
            }
        }
        if (!lp_Status)
            status = lp_Status;

        return status;
    }

    @Override
    public String getLanguageValue(String key, Locale locale) {
        return getLanguageItemByKey(key, locale).getResourceValue();
    }

    @Override
    public String getLanguageEntry(String key, Locale locale) {
        try {
            Language l = getLanguageItemByKey(key, locale);
            return l.getResourceValue();
        } catch (NoSuchEntityException e) {
            logger.debug("failed to get entry", e);
        }
        throw new MissingResourceException("Entry doesn't exist.", Language.class.getName(), key);
    }

    @Override
    public Language getLanguageItemByKey(String key, Locale locale) {
        String localeString = ITrackerResources.BASE_LOCALE;
        if (null != locale
                && !locale.equals(ITrackerResources.getLocale(ITrackerResources.BASE_LOCALE))) {
            localeString = locale.toString();
        }
        Language languageItem = languageDAO.findByKeyAndLocale(key, localeString);
        // TODO: obsolete code:
//        try {
//            languageItem = languageDAO.findByKeyAndLocale(key, ITrackerResources.BASE_LOCALE);
//        } catch (RuntimeException e) {
//            logger.debug("could not find {} with BASE", key);
//            languageItem = null;
//        }
//
//        if (null == locale) {
//            logger.debug("locale was null, returning BASE: {}", languageItem);
//            return languageItem;
//        }
//        try {
//            languageItem = languageDAO.findByKeyAndLocale(key, locale.getLanguage());
//        } catch (RuntimeException re) {
//            logger.debug("could not find {} with language {}", key, locale.getLanguage());
//        }
//        if (StringUtils.isNotEmpty(locale.getCountry())) {
//            try {
//                languageItem = languageDAO.findByKeyAndLocale(key, locale.toString());
//            } catch (RuntimeException ex) {
//                logger.debug("could not find {} with locale {}", key, locale);
//            }
//        }

        return languageItem;

    }

    public List<Language> getLanguageItemsByKey(String key) {
            List<Language> languageItems = languageDAO.findByKey(key);

        return languageItems;
    }

    public Language updateLanguageItem(Language language) {
        Language languageItem;

        try {
            languageItem = languageDAO.findByKeyAndLocale(language.getResourceKey(), language.getLocale());
            languageItem.setLocale(language.getLocale());
            languageItem.setResourceKey(language.getResourceKey());
            languageItem.setResourceValue(language.getResourceValue());
        } catch (NoSuchEntityException fe) {
            logger.debug("NoSuchEntityException: Language, now populating Language");
            languageItem = new Language();
            languageItem.setLocale(language.getLocale());
            languageItem.setResourceKey(language.getResourceKey());
            languageItem.setResourceValue(language.getResourceValue());
        }
        logger.debug("Start saveOrUpdate Language");
        languageDAO.saveOrUpdate(languageItem);
        logger.debug("Saved Language");
        return languageItem;
    }

    /**
     * Removes all <code>Language</code>s with the give key
     *
     * @param key The key to be removed
     */
    public boolean removeLanguageKey(String key) {
        boolean status = true;
        boolean lp_Status = true;

        // find all <code>Language</code>s for the given key
        List<Language> languageItems = languageDAO.findByKey(key);

        for (Iterator<Language> iter = languageItems.iterator(); iter.hasNext(); ) {
            // delete current item
            Language language = (Language) iter.next();
            try {
                this.languageDAO.delete(language);
            } catch (Exception ex) {
                lp_Status = false;
            }
        }
        if (!lp_Status)
            status = lp_Status;

        return status;
    }

    /**
     * Removes the <code>Language</code> passed as parameter
     *
     * @param language The <code>Language</code> to remove
     */
    public void removeLanguageItem(Language language) {

        Language languageItem = languageDAO.findByKeyAndLocale(language.getResourceKey(), language.getLocale());

        if (languageItem != null) {
            // delete item
            this.languageDAO.delete(languageItem);
        }
    }

    public String[] getSortedKeys() {

        int i = 0;
        Collection<Language> items = languageDAO.findByLocale(ITrackerResources.BASE_LOCALE);
        String[] sortedKeys = new String[items.size()];

        for (Iterator<Language> iter = items.iterator(); iter.hasNext(); i++) {
            Language item = (Language) iter.next();
            sortedKeys[i] = item.getResourceKey();
        }

        // Now sort the list of keys in a logical manner

        Arrays.sort(sortedKeys);
        return sortedKeys;

    }

    public HashMap<String, String> getDefinedKeys(String locale) {

        HashMap<String, String> keys = new HashMap<String, String>();

        if (locale == null || locale.equals("")) {
            locale = ITrackerResources.BASE_LOCALE;
        }


        Collection<Language> items = languageDAO.findByLocale(locale);
        for (Iterator<Language> iter = items.iterator(); iter.hasNext(); ) {
            Language item = iter.next();
            keys.put(item.getResourceKey(), item.getResourceValue());
        }


        return keys;

    }

    public List<NameValuePair> getDefinedKeysAsArray(String locale) {
        NameValuePair[] keys = null;
        if (locale == null || locale.equals("")) {
            locale = ITrackerResources.BASE_LOCALE;
        }

        int i = 0;
        Collection<Language> items = languageDAO.findByLocale(locale);
        keys = new NameValuePair[items.size()];

        for (Iterator<Language> iter = items.iterator(); iter.hasNext(); i++) {
            Language item = (Language) iter.next();
            keys[i] = new NameValuePair(item.getResourceKey(), item.getResourceValue());
        }

        Arrays.sort(keys);
        return Arrays.asList(keys);

    }

    public int getNumberDefinedKeys(String locale) {

        return getDefinedKeys(locale).size();

    }

    public List<Language> getLanguage(Locale locale) {
        Map<String, String> language = new HashMap<String, String>();
        if (locale == null) {
            locale = new Locale("");
        }
        String localeString = (locale.toString().equals("") ? ITrackerResources.BASE_LOCALE : locale.toString());

        Collection<Language> items = languageDAO.findByLocale(localeString);
        for (Language item : items) {
            language.put(item.getResourceKey(), item.getResourceValue());
        }

        Language[] languageArray = new Language[language.size()];
        int i = 0;

        for (String key : language.keySet()) {
            languageArray[i] = new Language(localeString, key, language.get(key));
            i++;
        }
        return Arrays.asList(languageArray);
    }

    public Properties getLanguageProperties(Locale locale) {
        Properties properties = new Properties();
        List<Language> lang = getLanguage(locale);
        for (Language l : lang) {
            properties.put(l.getResourceKey(), l.getResourceValue());
        }
        return properties;
    }

    public Map<String, List<String>> getAvailableLanguages() {

        final TreeMap<String, List<String>> languages = new TreeMap<>();
        final List<Configuration> locales = getConfigurationItemsByType(Configuration.Type.locale);

        for (int i = 0; i < locales.size(); i++) {
            String baselocalestring = locales.get(i).getValue();
            if (baselocalestring.length() == 2) {
                List<String> languageList = new ArrayList<>();
                final String l = baselocalestring;

                languageList.addAll(
                        CollectionUtils.collect(locales, new Transformer() {
                            @Override
                            public Object transform(Object input) {
                                String val = ((Configuration) input).getValue();
                                if (val.length() > 2 &&
                                        val.startsWith(l + "_")) {
                                    return val;
                                }
                                return null;
                            }
                        }));
                CollectionUtils.filter(languageList, NotNullPredicate.getInstance());
                languages.put(baselocalestring, languageList);
            }
        }


        return languages;

    }

    @SuppressWarnings("unchecked")
    public int getNumberAvailableLanguages() {

        int numLanguages = 0;
        Map<String, List<String>> availableLanguages = getAvailableLanguages();

        for (Iterator iter = availableLanguages.keySet().iterator(); iter.hasNext(); ) {
            List<List> languages = new ArrayList<>();
            List list = availableLanguages.get(iter.next());
            languages.add(list);

            if (languages != null && languages.size() > 0) {
                numLanguages += languages.size();
            } else {
                numLanguages += 1;
            }

        }

        return numLanguages;

    }

    public void updateLanguage(Locale locale, List<Language> items) {

        if (locale != null && items != null) {
            Configuration configItem = new Configuration(Configuration.Type.locale, locale
                    .toString(), getItrackerVersion());
            updateLanguage(locale, items, configItem);

        }

    }

    public void updateLanguage(Locale locale, List<Language> items, Configuration configItem) {
        for (int i = 0; i < items.size(); i++) {

            if (items.get(i) != null) {
                updateLanguageItem(items.get(i));
            }
        }
        removeConfigurationItems(configItem);
        createConfigurationItem(configItem);
    }

    public SystemConfiguration getSystemConfiguration(Locale locale) {

        SystemConfiguration config = new SystemConfiguration();

        // Load the basic system configuration

        List<Configuration> resolutions = getConfigurationItemsByType(Configuration.Type.resolution);

        for (int i = 0; i < resolutions.size(); i++) {

            resolutions.get(i).setName(ITrackerResources.getString(SystemConfigurationUtilities
                    .getLanguageKey(resolutions.get(i)), locale));

        }

        config.setResolutions(resolutions);

        List<Configuration> severities = getConfigurationItemsByType(Configuration.Type.severity);

        for (int i = 0; i < severities.size(); i++) {

            severities.get(i).setName(ITrackerResources.getString(SystemConfigurationUtilities
                    .getLanguageKey(severities.get(i)), locale));

        }

        config.setSeverities(severities);

        List<Configuration> statuses = getConfigurationItemsByType(Configuration.Type.status);

        for (int i = 0; i < statuses.size(); i++) {

            statuses.get(i).setName(ITrackerResources.getString(SystemConfigurationUtilities.getLanguageKey(statuses.get(i)),
                    locale));

        }

        config.setStatuses(statuses);

        List<CustomField> customFields = getCustomFields();

        config.setCustomFields(customFields);


        // Now set the system version

        config.setVersion(getItrackerVersion());

        return config;

    }


    public boolean initializeLocale(String locale, boolean forceReload) {
        boolean result = false;

        Configuration localeConfig = new Configuration(Configuration.Type.locale, locale,
                getItrackerVersion());

        if (!isConfigurationItemUpToDate(localeConfig) || forceReload) {

            logger.debug("Loading database with locale " + locale);

            removeConfigurationItems(localeConfig);
//                
            createConfigurationItem(localeConfig);
//                
            ITrackerResources.clearBundle(ITrackerResources.getLocale(locale));

            result = true;


        }

        return result;

    }

    public void initializeConfiguration() {

        // TODO when current version is outdated?
        long current = SystemConfigurationUtilities.getVersionAsLong(getItrackerVersion());

        long initialized = SystemConfigurationUtilities.getVersionAsLong(getInitializedVersionString());

        if (0 == initialized) {

            logger.info("System does not appear to be initialized, initializing system configuration.");

            ResourceBundle baseLanguage = ITrackerResources.getBundle(ITrackerResources.getLocale(ITrackerResources.BASE_LOCALE));
            getLanguage(ITrackerResources.getLocale(ITrackerResources.BASE_LOCALE));

            if (baseLanguage == null) {

                throw new IllegalStateException(
                        "Languages must be initialized before the system configuration can be loaded.");

            }

            // Remove any previous configuration information, possibly left
            // over from previous failed initialization

            logger.debug("Removing previous incomplete initialization information.");

            removeConfigurationItems(Configuration.Type.status);

            removeConfigurationItems(Configuration.Type.severity);

            removeConfigurationItems(Configuration.Type.resolution);

            Set<String> keys = baseLanguage.keySet();
            for (final String key : keys) {
                if (key.startsWith(ITrackerResources.KEY_BASE_RESOLUTION)) {

                    try {

                        String resolutionString = key.substring(20);
                        if (logger.isDebugEnabled()) {
                            logger.debug("Adding new configuration resolution value: " + resolutionString);
                        }
                        int resolutionNumber = Integer.parseInt(resolutionString);

                        createConfigurationItem(new Configuration(
                                Configuration.Type.resolution, resolutionString, getItrackerVersion(),
                                resolutionNumber));

                    } catch (RuntimeException e) {

                        logger.error("Unable to load resolution value: " + key, e);
                        throw e;

                    }

                }

                if (key.startsWith(ITrackerResources.KEY_BASE_SEVERITY)) {

                    try {

                        String severityString = key.substring(18);

                        logger.debug("Adding new configuration severity value: " + severityString);

                        int severityNumber = Integer.parseInt(severityString);

                        createConfigurationItem(new Configuration(Configuration.Type.severity,
                                severityString, getItrackerVersion(), severityNumber));

                    } catch (RuntimeException e) {

                        logger.error("Unable to load severity value: " + key, e);
                        throw e;
                    }

                }

                if (key.startsWith(ITrackerResources.KEY_BASE_STATUS)) {

                    try {

                        String statusString = key.substring(16);

                        logger.debug("Adding new configuration status value: " + statusString);

                        int statusNumber = Integer.parseInt(statusString);

                        createConfigurationItem(new Configuration(Configuration.Type.status,
                                statusString, getItrackerVersion(), statusNumber));
                    } catch (RuntimeException e) {
                        logger.error("Unable to load status value: " + key, e);
                        throw e;
                    }
                }
            }


            createConfigurationItem(new Configuration(Configuration.Type.initialized, "1",
                    getItrackerVersion()));
        }


    }

    public LanguageDAO getLanguageDAO() {
        return languageDAO;
    }

    public ConfigurationDAO getConfigurationDAO() {
        return configurationDAO;
    }

    public CustomFieldDAO getCustomFieldDAO() {
        return customFieldDAO;
    }

    public CustomFieldValueDAO getCustomFieldValueDAO() {
        return customFieldValueDAO;
    }

    public WorkflowScriptDAO getWorkflowScriptDAO() {
        return workflowScriptDAO;
    }

    public String getSystemBaseURL() {
        return getProperty(PNAME_SYSTEM_BASE_URL);
    }


    /**
     * This method will attempt to load all of the locales defined in the
     * ITracker.properties file, and add them to the database if they don't
     * already exist.
     *
     * @param forceReload if true, it will reload the languages from the property files
     *                    even if they are listed as being up to date
     */
    public void initializeAllLanguages(boolean forceReload) {
        Set<String> definedLocales = new LinkedHashSet<>();

        initializeLocale(ITrackerResources.BASE_LOCALE,
                forceReload);

        String definedLocalesString;
        try {
            definedLocalesString =
                    getProperty("available_locales", ITrackerResources.getDefaultLocale());
        } catch (RuntimeException e) {
            definedLocalesString = ITrackerResources.getString(ITrackerResources.getDefaultLocale());
        }
        if (definedLocalesString != null) {
            StringTokenizer token = new StringTokenizer(definedLocalesString, ",");
            while (token.hasMoreTokens()) {
                String locale = StringUtils.trim(token.nextToken());
                if (locale.length() == 5 && locale.indexOf('_') == 2) {
                    definedLocales.add(locale.substring(0, 2));
                }
                definedLocales.add(locale);
            }
        }

//        // apply configuration locales
//        for (Configuration c: getConfigurationItemsByType(Configuration.Type.locale)) {
//            if (!definedLocales.contains(c.getValue())) {
//                logger.info("removing language configuration from database: {}, not in: {}", c, definedLocales);
//                removeConfigurationItems(c);
//            }
//        }
        for (String locale : definedLocales) {
            initializeLocale(locale, forceReload);
        }
    }
}
