    create index componentbeanStatusIdx on componentbean (status);

    create index componentbeanNameIdx on componentbean (name);

    create index configurationbeanOrderIdx on configurationbean (item_order);

    create index configurationbeanTypeIdx on configurationbean (item_type);

    create index configurationbeanValIdx on configurationbean (item_value);

    create index configurationbeanVersionIdx on configurationbean (item_version);

    create index customfieldbeanFieldTypeIdx on customfieldbean (field_type);
    
    create index typeIdx on issueattachmentbean (attachment_type);

    create index issueattachmentbeanFileNameIdx on issueattachmentbean (file_name);

    create index issuebeanResolutionIdx on issuebean (resolution);

    create index issuebeanStatusIdx on issuebean (status);

    create index issuebeanSeverityIdx on issuebean (severity);

    create index issuerelationbeanMatchingRelationIdIdx on issuerelationbean (matching_relation_id);

    create index issuerelationbeanTypeIdx on issuerelationbean (relation_type);

    create index languagebeanLocaleIdx on languagebean (locale);

    create index languagebeanKeyIdx on languagebean (resource_key);

    create index notificationRoleIdx on notificationbean (user_role);

    create index permisionTypeIdx on permissionbean (permission_type);

    create index projectbeanNameIdx on projectbean (name);

    create index projectbeanStatusIdx on projectbean (status);

    create index userbeanRegTypeIdx on userbean (registration_type);

    create index userbeanSUIdx on userbean (super_user);

    create index loginIdx on userbean (login);

    create index userbeanStatusIdx on userbean (status);

    create index versionbeanNumberIdx on versionbean (version_number);
