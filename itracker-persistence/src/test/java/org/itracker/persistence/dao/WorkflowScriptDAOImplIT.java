package org.itracker.persistence.dao;

import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.model.WorkflowScript;
import org.junit.Test;

import java.util.List;

import static org.itracker.Assert.*;
public class WorkflowScriptDAOImplIT extends AbstractDependencyInjectionTest {

    private WorkflowScriptDAO workflowScriptDAO;

    @Test
    public void testFindByPrimaryKey() {
        WorkflowScript workflowScript = workflowScriptDAO.findByPrimaryKey(1);
        assertNotNull(workflowScript);
        assertNotNull("workflowScript.id", workflowScript.getId());
        assertEquals("workflowScript.id", new Integer(1), workflowScript.getId());
        assertEquals("workflowScript.name", "Script Name", workflowScript.getName());
        assertEquals("workflowScript.script", "Script Data", workflowScript.getScript());
        assertEquals("workflowScript.event", 1, workflowScript.getEvent());
    }

    @Test
    public void testFindAll() {
        List<WorkflowScript> workflowScripts = workflowScriptDAO.findAll();
        assertNotNull(workflowScripts);
        assertEquals("total workflow scripts", 2, workflowScripts.size());
    }

    @Override
    public void onSetUp() throws Exception {
        super.onSetUp();

        workflowScriptDAO = (WorkflowScriptDAO) applicationContext.getBean("workflowScriptDAO");
    }

    @Override
    protected String[] getDataSetFiles() {
        return new String[]{
                "dataset/workflowscriptbean_dataset.xml"
        };
    }


}
