package org.itracker.selenium;

import junit.framework.Assert;
import org.apache.log4j.Level;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Makes sure selenium is working.
 * Will also check the itracker website.
 */
public class SeleniumPreTest {
    private final Logger logger = LoggerFactory.getLogger(SeleniumPreTest.class);

   @Test
    public void testITrackerSiteSelenium() throws Exception {

        org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(getClass());
        log.log("Test", Level.INFO, "TEST", null);
        log.debug("hello");
        WebDriver driver = SeleniumManager.getWebDriver();
        if (logger.isDebugEnabled()) {
            logger.debug("testITrackerSiteSelenium has a webdriver: {}", driver);
        }
        driver.get("http://itracker.sourceforge.net");
        driver.findElement(By.cssSelector("body #banner"));
        driver.findElement(By.cssSelector("body #breadcrumbs"));
        driver.findElement(By.cssSelector("body #leftColumn"));
        driver.findElement(By.cssSelector("body #bodyColumn"));
        driver.findElement(By.cssSelector("body #footer"));


        driver.get("http://itracker.sourceforge.net/license.html");
        final String license = driver.findElement(By.xpath("//h3[a[@name=\"GNU_Lesser_General_Public_License\"]]")).getText();
        Assert.assertEquals("project license", "GNU Lesser General Public License", license);

    }


}
