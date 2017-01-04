package org.itracker.persistence.dao;

import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.model.Notification;
import org.itracker.model.Notification.Role;
import org.junit.Test;

import java.util.List;

import static org.itracker.Assert.*;
public class NotificationDAOImplIT extends AbstractDependencyInjectionTest {

    private NotificationDAO notificationDAO;

    @Test
    public void testFindById() {

        Notification notification = notificationDAO.findById(1);

        assertNotNull(notification);

        assertEquals(1, notification.getIssue().getId().intValue());
        assertEquals(2, notification.getUser().getId().intValue());
        assertEquals(Role.CREATOR, notification.getRole());

    }

    @Test
    public void testFindByIssueId() {

        List<Notification> notifications = notificationDAO.findByIssueId(1);

        assertNotNull(notifications);
        assertEquals(1, notifications.size());

    }

    @Override
    public void onSetUp() throws Exception {
        super.onSetUp();

        notificationDAO = (NotificationDAO) applicationContext.getBean("notificationDAO");

    }

    protected String[] getDataSetFiles() {
        return new String[]{
                "dataset/userpreferencesbean_dataset.xml",
                "dataset/userbean_dataset.xml",
                "dataset/projectbean_dataset.xml",
                "dataset/versionbean_dataset.xml",
                "dataset/issuebean_dataset.xml",
                "dataset/notificationbean_dataset.xml",
                "dataset/issueactivitybean_dataset.xml"
        };
    }


}
