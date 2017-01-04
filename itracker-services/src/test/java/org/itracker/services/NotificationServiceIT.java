package org.itracker.services;

import org.itracker.model.Issue;
import org.itracker.model.Notification;
import org.itracker.model.Notification.Role;
import org.itracker.model.Notification.Type;
import org.itracker.persistence.dao.IssueDAO;
import org.itracker.persistence.dao.NotificationDAO;
import org.itracker.persistence.dao.UserDAO;
import org.junit.Test;

import javax.mail.internet.InternetAddress;
import java.util.List;

import static org.itracker.Assert.*;
public class NotificationServiceIT extends AbstractServicesIntegrationTest {

    private NotificationService notificationService;

    // DAOs
    private NotificationDAO notificationDAO;
    private IssueDAO issueDAO;
    private UserDAO userDAO;

    @Test
    public void testSendNotification() {
        try {
            notificationService.sendNotification(issueDAO.findByPrimaryKey(1), Type.CREATED, "http://");
        } catch (Exception e) {
            fail(e.getMessage());
        }

        try {
            notificationService.sendNotification(notificationDAO.findById(1), Type.CREATED, "http://");
        } catch (Exception e) {
            fail(e.getMessage());
        }

        try {
            notificationService.sendNotification((Issue) null, Type.CREATED, "http://");
        } catch (Exception e) {
            fail(e.getMessage());
        }

        try {
            notificationService.sendNotification(
                    issueDAO.findByPrimaryKey(1),
                    Type.CREATED,
                    "http://",
                    new InternetAddress[]{},
                    2);
        } catch (Exception e) {
            fail(e.getMessage());
        }

    }

    @Test
    public void testAddIssueNotification() {
        Notification notification = new Notification(
                userDAO.findByPrimaryKey(2),
                issueDAO.findByPrimaryKey(1),
                Role.ANY
        );
        boolean added = notificationService.addIssueNotification(notification);
        assertTrue("notification added", added);
        assertNotNull("notification.id", notification.getId());

        notification = new Notification(
                userDAO.findByPrimaryKey(2),
                issueDAO.findByPrimaryKey(2),
                Role.ANY
        );
        added = notificationService.addIssueNotification(notification);
        assertTrue("notification added", added);
        assertNotNull("notification.id", notification.getId());
    }

    @Test
    public void testGetIssueNotifications() {
        Issue issue1 = issueDAO.findByPrimaryKey(1);
        Issue issue2 = issueDAO.findByPrimaryKey(2);

        //
        // Issue 1
        //
        List<Notification> notifications = notificationService.getIssueNotifications(issue1);
        assertNotNull(notifications);
        assertEquals("notifications.size", 3, notifications.size()); // 1 in db + 2 primary (owner + creator)

        notifications = notificationService.getIssueNotifications(issue1, true, true);
        assertNotNull(notifications);
        // returns 1, see testPrimaryIssueNotifications()
//		assertEquals( "notifications.size", 2, notifications.size() ); // 2 primary (owner + creator)

        //
        // Issue 2
        //

        notifications = notificationService.getIssueNotifications(issue2);
        assertNotNull(notifications);
        assertEquals("notifications.size", 2, notifications.size());// 2 primary (owner + creator)


    }

    @Test
    //TODO: should be different owner and creator, projectowners
    public void testPrimaryIssueNotifications() {
        Issue issue = issueDAO.findByPrimaryKey(1);
        assertEquals(0, issue.getProject().getOwners().size());
        assertTrue(issue.getOwner() != null);
        issueDAO.save(issue);

        List<Notification> notifications =
                notificationService.getPrimaryIssueNotifications(issue);
        assertNotNull(notifications);
        // only the owner
        assertEquals("notifications.size", 1, notifications.size());

        assertEquals("notifications.user", notifications.get(0).getUser(), issue.getOwner());


        issue.setOwner(null);
        issueDAO.save(issue);

        assertTrue(issue.getOwner() == null);

        // only the creator
        assertEquals("notifications.size", 1, notifications.size());

        assertEquals("notifications.user", notifications.get(0).getUser(), issue.getCreator());
    }

    @Test
    public void testHasIssueNotification() {

        Issue issue1 = issueDAO.findByPrimaryKey(1);

        // Passing null
        assertFalse(notificationService.hasIssueNotification(null, 2));
        assertFalse(notificationService.hasIssueNotification(issue1, (Integer)null));

        //
        // Issue 1
        //
        boolean hasIssueNotification = notificationService.hasIssueNotification(issue1, 2);
        assertTrue("issue 1, user 2, hasIssueNotification", hasIssueNotification);

        hasIssueNotification = notificationService.hasIssueNotification(issue1, 3);
        assertFalse("issue 1, user 2, hasIssueNotification", hasIssueNotification);

        // Test with Role
        hasIssueNotification = notificationService.hasIssueNotification(issue1, 2, Role.ANY);
        assertTrue("issue 1, user 2, hasIssueNotification", hasIssueNotification);

        hasIssueNotification = notificationService.hasIssueNotification(issue1, 3, Role.ANY);
        assertFalse("issue 1 user 3, hasIssueNotification", hasIssueNotification);
    }


    @Test
    public void testRemoveIssueNotification() {
        Notification notification = new Notification(
                userDAO.findByPrimaryKey(3),
                issueDAO.findByPrimaryKey(1),
                Role.ANY
        );
        notificationDAO.save(notification);
        Integer id = notification.getId();
        assertNotNull(id);

        boolean removed = notificationService.removeIssueNotification(id);
        assertTrue("removed", removed);
        assertNull("removed notification", notificationDAO.findById(id));
    }


    @Override
    public void onSetUp() throws Exception {
        super.onSetUp();
        this.notificationService = (NotificationService) applicationContext.getBean("notificationService");
        this.issueDAO = (IssueDAO) applicationContext.getBean("issueDAO");
        this.userDAO = (UserDAO) applicationContext.getBean("userDAO");
        this.notificationDAO = (NotificationDAO) applicationContext.getBean("notificationDAO");
    }

    @Override
    protected String[] getDataSetFiles() {
        return new String[]{
                "dataset/userpreferencesbean_dataset.xml",
                "dataset/userbean_dataset.xml",
                "dataset/projectbean_dataset.xml",
                "dataset/versionbean_dataset.xml",
                "dataset/issuebean_dataset.xml",
                "dataset/notificationbean_dataset.xml",
        };
    }


}
