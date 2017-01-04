package org.itracker.persistence.dao;

import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.model.IssueActivity;
import org.itracker.model.IssueActivityType;
import org.junit.Test;

import java.util.List;

import static org.itracker.Assert.*;
public class IssueActivityDAOImplIT extends AbstractDependencyInjectionTest {

    //	private IssueDAO issueDAO;
    private IssueActivityDAO issueActivityDAO;
//	private UserDAO userDAO;
//	private UserService userService;

    @Test
    public void testFindByIssueId() {

        List<IssueActivity> activities = issueActivityDAO.findByIssueId(2);
        assertNotNull("issue#2.activities", activities);

        assertEquals("issue#2.activities.size", 1, activities.size());

        IssueActivity issueActivity = activities.get(0);
        assertEquals("issue#2.activities[].issueId", Integer.valueOf(2), issueActivity.getIssue().getId());


    }

    @Test
    public void testFindById() throws Exception {

        IssueActivity activity = issueActivityDAO.findById(1);
        assertNotNull("issueActivity#1", activity);
        assertEquals("issueActivity#1.description",
                "user 2 created this issue", activity.getDescription());
        assertEquals("issueActivity#1.type", IssueActivityType.ISSUE_CREATED,
                activity.getActivityType());

    }

    @Test
    public void testFindByIssueIdAndNotification() {
        Integer issueId = 1;
        boolean notificationSent = true;

        List<IssueActivity> results = issueActivityDAO.findByIssueIdAndNotification(issueId, notificationSent);
        assertNotNull("issue#1.notificatioSent#true.activities", results);
        assertEquals("issue#1.notificatioSent#true.activities.size", results.size(), 1);

        IssueActivity got = results.get(0);
        assertNotNull("issue#1.notificatioSent#true.activities[0]", got);
        assertEquals("issue#1.notificatioSent#true.activities[0].notificationSent", notificationSent, got.getNotificationSent());
        assertEquals("issue#1.notificatioSent#true.activities[0].issueId", issueId, got.getIssue().getId());

        notificationSent = false;
        results = issueActivityDAO.findByIssueIdAndNotification(issueId, notificationSent);

        assertNotNull("issue#1.notificatioSent#false.activities", results);
        assertEquals("issue#1.notificatioSent#false.activities.size", results.size(), 0);

    }


    @Override
    public void onSetUp() throws Exception {
        super.onSetUp();

//		issueDAO = (IssueDAO) applicationContext.getBean("issueDAO");
        issueActivityDAO = (IssueActivityDAO) applicationContext
                .getBean("issueActivityDAO");
//		userDAO = (UserDAO) applicationContext.getBean("userDAO");

//		userService = (UserService) applicationContext.getBean("userService");

    }

    protected String[] getDataSetFiles() {
        return new String[]{"dataset/userpreferencesbean_dataset.xml",
                "dataset/userbean_dataset.xml",
                "dataset/projectbean_dataset.xml",
                "dataset/versionbean_dataset.xml",
                "dataset/issuebean_dataset.xml",
                "dataset/issueactivitybean_dataset.xml"};
    }


}
