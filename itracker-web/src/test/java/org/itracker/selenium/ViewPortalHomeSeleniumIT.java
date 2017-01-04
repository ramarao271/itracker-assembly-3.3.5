package org.itracker.selenium;

import org.junit.Test;
import org.openqa.selenium.By;

import java.io.IOException;
/**
 * Check the content of PortalHome page with some data available.
 *
 * @author Andrey Sergievskiy <seas@andreysergievskiy.com>
 */
public class ViewPortalHomeSeleniumIT extends AbstractSeleniumTestCase {
    /**
     * Verifies the successfull login case with valid login/password.
     * <p/>
     * 0. Exit all available http sessions.
     * 1. Login into the system with particular login (admin_test1).
     * 2. Check that "Unassigned Issues" section is present at the page.
     * 3. Check that number of unassigned issues is 2 (as we defined in
     * database before running this test).
     * 4. Check all unassigned issues we expect to be in the system are
     * shown at the page (check them one by one).
     * 5. Check that "Created Issues" section is present at the page.
     * 6. Check that number of created issues is 4 (as we defined in
     * database before running this test).
     * 7. Check all all create issues we expect to be in the system are
     * shown at the page (check them one by one).
     * 8. Check that "Watched Issues" section is present at the page.
     * 9. Check that number of watched items is 0.
     */
    @Test
    public void testViewHomePage() throws IOException {
        closeSession();
        driver.get("http://" + applicationHost + ":" + applicationPort + "/"
                + applicationPath);

        login("admin_test1", "admin_test1");

        assertElementPresent(By.name("id"));
        assertElementPresent(By.id("unassignedIssues"));
        assertElementCountEquals(2, By.xpath("//*[starts-with(@id, 'unassignedIssue.')]"));

       assertElementPresent(By.xpath("//*[starts-with(@id,'unassignedIssue.1')]" +
               "/td[contains(text(),'1')]/.." +
                "/td[normalize-space(text())='test_name']/.." +
                "/td[normalize-space(text())='test_description']"));

        assertElementPresent(By.xpath("//*[starts-with(@id,'unassignedIssue.2')]" +
                "/td[contains(text(),'2')]/.." +
                "/td[normalize-space(text())='test_name']/.." +
                "/td[normalize-space(text())='test_description 2']"));

        assertElementPresent(By.id("createdIssues"));
        assertElementCountEquals(4, By.xpath("//*[starts-with(@id, 'createdIssue.')]"));

        assertElementPresent(By.xpath("//*[starts-with(@id,'createdIssue.1')]" +
                "/td[contains(text(),'1')]/.." +
                "/td[normalize-space(text())='test_name']/.." +
                "/td[normalize-space(text())='test_description']"));

        assertElementPresent(By.xpath("//*[starts-with(@id,'createdIssue.2')]" +
                "/td[contains(text(),'2')]/.." +
                "/td[normalize-space(text())='test_name']/.." +
                "/td[normalize-space(text())='test_description 2']"));

        assertElementPresent(By.xpath("//*[starts-with(@id,'createdIssue.3')]" +
                "/td[contains(text(),'3')]/.." +
                "/td[normalize-space(text())='test_name']/.." +
                "/td[normalize-space(text())='test_description 3']"));

        assertElementPresent(By.xpath("//*[starts-with(@id,'createdIssue.4')]" +
                "/td[contains(text(),'4')]/.." +
                "/td[normalize-space(text())='test_name']/.." +
                "/td[normalize-space(text())='test_description 4']"));

        assertElementPresent(By.id("watchedIssues"));
        assertElementNotPresent(By.xpath("//*[starts-with(@id, 'watchedIssue.')]"));
    }


    @Override
    protected String[] getDataSetFiles() {
        return new String[]{
                "dataset/languagebean_init_dataset.xml",
                "dataset/languagebean_dataset.xml",
                "dataset/userpreferencesbean_dataset.xml",
                "dataset/userbean_dataset.xml",
                "dataset/projectbean_dataset.xml",
                "dataset/permissionbean_dataset.xml",
                "dataset/versionbean_dataset.xml",
                "dataset/issuebean_dataset.xml"
        };
    }

}
