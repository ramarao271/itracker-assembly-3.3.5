/* The following statements have been used by Marky Goldstein to migrate his existing MySQL database: */

/* It's a general guide how to setup a migration sql script: */

/*
Insert the value
"org.itracker.web.reports.predefined.ITrackerJasperReport"
in table "reportbean" to column  "class_name"

Insert the value
"org.itracker.web.reports.predefined.ITrackerJFreeReport"
in table "reportbean" to column  "class_name"
*/

/* START SQL SCRIPT */

CREATE TABLE issuerelationbean (
  id                    INT      NOT NULL auto_increment,
  issue_id              INT,
  rel_issue_id          INT,
  relation_type         INT,
  matching_relation_id  INT,
  create_date           DATETIME,
  last_modified         DATETIME,
  PRIMARY KEY(id)
) DEFAULT CHARACTER SET utf8;

CREATE TABLE workflowscriptbean (
  id                    INT       NOT NULL auto_increment,
  script_name           VARCHAR(255),
  event_type            INT,
  script_data           TEXT,
  create_date           DATETIME,
  last_modified         DATETIME,
  PRIMARY KEY(id)
) DEFAULT CHARACTER SET utf8;

CREATE TABLE projectscriptbean (
  id                    INT       NOT NULL auto_increment,
  project_id            INT,
  field_id              INT,
  script_id             INT,
  script_priority       INT,
  create_date           DATETIME,
  last_modified         DATETIME,
  PRIMARY KEY(id)
) DEFAULT CHARACTER SET utf8;

alter table languagebean change id id INT NOT NULL AUTO_INCREMENT; 

alter table configurationbean change id id INT NOT NULL AUTO_INCREMENT; 

alter table issuehistorybean change id id INT NOT NULL AUTO_INCREMENT;

alter table issuebean change id id INT NOT NULL AUTO_INCREMENT;

alter table versionbean change id id INT NOT NULL AUTO_INCREMENT;
UPDATE versionbean SET status = 1 WHERE status IS NULL;
alter table versionbean change status status INT NOT NULL;

alter table userpreferencesbean change id id INT NOT NULL AUTO_INCREMENT;
ALTER TABLE userpreferencesbean ADD use_text_actions INT NOT NULL AFTER remember_last_search ;

alter table userbean change id id INT NOT NULL AUTO_INCREMENT;

DELETE  FROM scheduledtaskbean; 
alter table scheduledtaskbean change id id INT NOT NULL AUTO_INCREMENT;

alter table projectbean change id id INT NOT NULL AUTO_INCREMENT;

alter table permissionbean change id id INT NOT NULL AUTO_INCREMENT;

alter table notificationbean change id id INT NOT NULL AUTO_INCREMENT;

alter table issuefieldbean change id id INT NOT NULL AUTO_INCREMENT;

alter table issueattachmentbean change id id INT NOT NULL AUTO_INCREMENT;

DELETE  FROM issueattachmentbean WHERE orig_file_name IS NULL;
alter table issueattachmentbean CHANGE issue_id issue_id INT NOT NULL;
alter table issueattachmentbean CHANGE orig_file_name orig_file_name VARCHAR(255) NOT NULL;
alter table issueattachmentbean CHANGE description description VARCHAR(255) NOT NULL;

alter table issueactivitybean change id id INT NOT NULL AUTO_INCREMENT;

alter table customfieldvaluebean change id id INT NOT NULL AUTO_INCREMENT;

alter table customfieldbean change id id INT NOT NULL AUTO_INCREMENT;

alter table componentbean change id id INT NOT NULL AUTO_INCREMENT;
UPDATE componentbean SET status = 1 WHERE status IS NULL;
alter table componentbean change status status INT NOT NULL;










