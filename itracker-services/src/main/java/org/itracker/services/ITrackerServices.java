package org.itracker.services;


/**
 * Service layer is a bit messy. The are *Factories, which work mainly as data
 * access objects, and *Handlers, that work as the service layer. It's messy
 * because it was a straight EJB migration, and they were not refactored yet.
 *
 * @author ricardo
 */
public interface ITrackerServices {

    IssueService getIssueService();

    UserService getUserService();

    ProjectService getProjectService();

    ConfigurationService getConfigurationService();

    ReportService getReportService();

    EmailService getEmailService();

    NotificationService getNotificationService();
}
