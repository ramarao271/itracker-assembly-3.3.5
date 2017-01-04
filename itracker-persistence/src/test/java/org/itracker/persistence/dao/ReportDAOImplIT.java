package org.itracker.persistence.dao;

import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.model.Report;
import org.junit.Test;

import java.util.List;

import static org.itracker.Assert.*;
public class ReportDAOImplIT extends AbstractDependencyInjectionTest {

    private ReportDAO reportDAO;

    @Test
    public void testFindByPrimaryKey() {
        Report report = reportDAO.findByPrimaryKey(1000);
        assertNotNull("report", report);
        assertNotNull("report.id", report.getId());
        assertEquals("report.id", new Integer(1000), report.getId());
        assertEquals("report.name", "DailyReport Report", report.getName());
    }

    @Test
    public void testFindAll() {
        List<Report> reports = reportDAO.findAll();
        assertNotNull("reports", reports);
        assertEquals("total reports", 1, reports.size());
    }

    @Override
    public void onSetUp() throws Exception {
        super.onSetUp();

        reportDAO = (ReportDAO) applicationContext.getBean("reportDAO");
    }

    @Override
    protected String[] getDataSetFiles() {
        return new String[]{
                "dataset/reportbean_dataset.xml",
        };
    }


}
