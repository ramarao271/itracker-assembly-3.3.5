package org.itracker.persistence.dao;

import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.model.ProjectScript;
import org.junit.Test;

import java.util.List;
import static org.itracker.Assert.*;

public class ProjectScriptDAOImplIT extends AbstractDependencyInjectionTest {

    private ProjectScriptDAO projectScriptDAO;

    @Override
    public void onSetUp() throws Exception {
        super.onSetUp();

        projectScriptDAO = (ProjectScriptDAO) applicationContext.getBean("projectScriptDAO");
    }

    @Test
    public void testFindByPrimaryKey() {
        ProjectScript projectScript = projectScriptDAO.findByPrimaryKey(1);
        assertNotNull(projectScript);
        assertNotNull("projectScript.id", projectScript.getId());
        assertEquals("projectScript.id", new Integer(1), projectScript.getId());
    }

    @Test
    public void testFindAll() {
        List<ProjectScript> projectScripts = projectScriptDAO.findAll();
        assertNotNull(projectScripts);
        assertEquals("total project scripts", 1, projectScripts.size());
    }

    @Test
    public void testFindByProject() {
        List<ProjectScript> projectScripts = projectScriptDAO.findByProject(2);
        assertNotNull(projectScripts);
        assertEquals("total project scripts for project#2", 1, projectScripts.size());

        projectScripts = projectScriptDAO.findByProject(3);
        assertNotNull(projectScripts);
        assertEquals("total project scripts for project#3", 0, projectScripts.size());
    }

    @Test
    public void testFindByProjectField() {
        List<ProjectScript> projectScripts = projectScriptDAO.findByProjectField(2, 1);
        assertNotNull(projectScripts);
        assertEquals("total project scripts for project#2, field#1", 1, projectScripts.size());

        projectScripts = projectScriptDAO.findByProjectField(2, 2);
        assertNotNull(projectScripts);
        assertEquals("total project scripts for project#2, field#2", 0, projectScripts.size());
    }

    @Override
    protected String[] getDataSetFiles() {
        return new String[]{
                "dataset/projectbean_dataset.xml",
                "dataset/workflowscriptbean_dataset.xml",
                "dataset/projectscriptbean_dataset.xml",

        };
    }


}
