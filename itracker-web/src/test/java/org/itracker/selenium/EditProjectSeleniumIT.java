package org.itracker.selenium;

import org.junit.Test;
import org.openqa.selenium.By;

import java.io.IOException;

/**
 * Smoketest for Edit Projects UI.
 *
 * @author ranks
 */
public class EditProjectSeleniumIT extends AbstractSeleniumTestCase {
    /**
     * 0. Exit all available http sessions.
     * 1. Login into the system with particular login (admin_test1).
     * 2. Check that "Project Admin" link is present at the "Portal Home" page.
     * 3. Click "Project Admin".
     * 4. Check that one "Project" element is present at the "Project Admin" page.
     * 5. Click "Edit Project".
     * 6. Check that one "Project Script" element is present.
     * 7. Click "Add New Projectscript".
     * 8. Add a new Projectscript relation to the project.
     * 9. Check that the project contains 2 "Project Script" relations now.
     */
    @Test
    public void testAddProjectScript() throws IOException {
        closeSession();
        driver.get("http://" + applicationHost + ":" + applicationPort + "/"
                + applicationPath);

        login("admin_test1", "admin_test1");

        assertElementPresent(By.name("projectadmin")).click();

        waitForPageToLoad();

        // open project admin edit view
        assertElementPresent(By.xpath("//a[contains(@href, 'action=update')][1]")).click();
        waitForPageToLoad();

        // 1 project scripts is assigned
        assertElementCountEquals(1, By.xpath("//a[contains(@href, 'removeprojectscript.do')]"));
        // open project admin edit projectscript view
        assertElementPresent(By.xpath("//a[contains(@href, 'editprojectscriptform.do')]")).click();
        waitForPageToLoad();

        // select the first script
        assertElementPresent(By.name("scriptItems(1)")).click();
        // save the script relation
        assertElementPresent(By.cssSelector("input[type='submit']")).click();
        waitForPageToLoad();

        // new project script is assigned
        assertElementCountEquals(2, By.xpath("//a[contains(@href, 'removeprojectscript.do')]"));

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
                "dataset/issuebean_dataset.xml",
                "dataset/workflowscriptbean_dataset.xml",
                "dataset/projectscriptbean_dataset.xml"
        };
    }
}
