package org.itracker.persistence.dao;

import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.model.Version;
import org.junit.Test;

import java.util.List;

import static org.itracker.Assert.*;
public class VersionDAOImplIT extends AbstractDependencyInjectionTest {

    private VersionDAO versionDAO;

    @Test
    public void testFindByPrimaryKey() {
        Version version = versionDAO.findByPrimaryKey(1);

        assertNotNull(version);
        assertEquals(Integer.valueOf(1), version.getId());
    }

    @Test
    public void testFindByProjectId() {
        List<Version> versions = versionDAO.findByProjectId(2);

        assertNotNull(versions);
        assertEquals(1, versions.size());

        Version version = versions.get(0);
        assertEquals(Integer.valueOf(1), version.getId());
    }

    @Override
    public void onSetUp() throws Exception {
        super.onSetUp();

        versionDAO = (VersionDAO) applicationContext.getBean("versionDAO");
    }

    protected String[] getDataSetFiles() {
        return new String[]{
                "dataset/userpreferencesbean_dataset.xml",
                "dataset/userbean_dataset.xml",
                "dataset/projectbean_dataset.xml",
                "dataset/permissionbean_dataset.xml",
                "dataset/versionbean_dataset.xml"
        };
    }


}
