package org.itracker.services;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.itracker.model.Report;
import org.itracker.persistence.dao.ReportDAO;
import org.junit.Test;
import org.springframework.dao.DataAccessException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.itracker.Assert.*;
public class ReportServiceImplIT extends AbstractServicesIntegrationTest {

    private static final Logger log = Logger.getLogger(ReportServiceImplIT.class);

    private ReportDAO reportDAO;

    /**
     * Object to be Tested: ReportService
     */
    private ReportService reportService;

    @Test
    public void testGetAllReports() {
        // defined in reportbean_dataset.xml
        try {
            List<Report> reports = reportService.getAllReports();

            assertNotNull("reports ", reports);
            assertEquals("reports size ", 1, reports.size());
            Report report = reports.get(0);
            assertEquals("id", 1000, report.getId().intValue());
            assertEquals("name", "DailyReport Report", report.getName());
            assertEquals("nameKey", "0001", report.getNameKey());
            assertEquals("description", "This is a daily report", report.getDescription());
            DateFormat format = new SimpleDateFormat("yy-MM-dd hh:mm:ss");
            try {
                assertEquals("createDate", format.parse("2004-01-01 13:00:00"), report.getCreateDate());
                assertEquals("modified date", format.parse("2005-01-01 15:00:00"), report.getLastModifiedDate());
            } catch (ParseException e) {
                log.error("testGetAllReports: failed to parse date for assertion", e);
                fail("failed to parse date for assertion: " + e.getMessage());
            }
            assertNotNull("contents", report.getFileData());
            assertTrue("contents.size", report.getFileData().length > 1);

        } catch (Exception e) {
            log.error("testGetAllReports: failed to getAllReports in ReportService", e);
            fail("failed to getAllReports: " + e.getMessage());
        }

    }

    @Test
    public void testGetNumberReports() {
        try {
            // defined in reportbean_dataset.xml
            assertEquals("reports size", 1, reportService.getNumberReports());
        } catch (Exception e) {
            log.error("testGetNumberReports: failed to getNumberReports in ReportService", e);
            fail("failed to getNumberReports: " + e.getMessage());
        }
    }

    @Test
    public void testGetReportDAO() {
        assertNotNull("getReportDAO", reportService.getReportDAO());
    }

    @Test
    public void testCreateReport() {
        //test save
        Report report = new Report();
        report.setName("weekly report");
        report.setNameKey("0002");
        report.setDescription("This is a weekly report");
        report.setClassName("xxx.xxx.Weekly");
        report.setFileData(new byte[]{1,2,3});
        Report result = reportService.createReport(report);
        Report reportFind = reportDAO.findByPrimaryKey(result.getId());
        assertNotNull(reportFind);
        assertEquals("id", result.getId(), reportFind.getId());
        assertEquals("name", "weekly report", reportFind.getName());

        //test update
        report.setName("monthly report");
        Report result1 = reportService.createReport(report);
        reportFind = reportDAO.findByPrimaryKey(result1.getId());
        assertEquals("name", "monthly report", reportFind.getName());
        for (int b : new byte[]{1,2,3}) {
            assertEquals("fileData byte " + b, b, reportFind.getFileData()[b-1]);
        }

        //test null
        try {
            report = null;
            reportService.createReport(report);
            fail("do not throw DataAccessException ");
        } catch (DataAccessException e) {
            assertTrue(true);
        }

    }


    @Override
    public void onSetUp() throws Exception {
        super.onSetUp();
        reportService = (ReportService) applicationContext.getBean("reportService");
        this.reportDAO = (ReportDAO) applicationContext.getBean("reportDAO");

    }

    protected String[] getDataSetFiles() {
        return new String[]{
                "dataset/reportbean_dataset.xml"
        };
    }

}
