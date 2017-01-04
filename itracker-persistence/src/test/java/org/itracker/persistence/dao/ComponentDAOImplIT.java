package org.itracker.persistence.dao;

import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.model.Component;
import org.itracker.model.Status;
import org.junit.Test;

import java.util.List;
import static org.itracker.Assert.*;

public class ComponentDAOImplIT extends AbstractDependencyInjectionTest {

    private ComponentDAO componentDAO;

    @Test
    public void testFindById() {
        Component component = componentDAO.findById(1);

        assertNotNull(component);

        assertEquals(2, component.getProject().getId().intValue());
        assertEquals("Test Name", component.getName());
        assertEquals("Test Description", component.getDescription());
        assertEquals(Status.ACTIVE, component.getStatus());
    }

    @Test
    public void testFindByProject() {
        List<Component> components = componentDAO.findByProject(2);

        assertEquals(2, components.size());

        Component component = components.get(0);

        assertEquals(1, component.getId().intValue());
        assertEquals("Test Name", component.getName());
        assertEquals("Test Description", component.getDescription());
        assertEquals(Status.ACTIVE, component.getStatus());
    }

    @Override
    public void onSetUp() throws Exception {
        super.onSetUp();

        componentDAO = (ComponentDAO) applicationContext.getBean("componentDAO");
    }

    protected String[] getDataSetFiles() {
        return new String[]{
                "dataset/projectbean_dataset.xml",
                "dataset/componentbean_dataset.xml"
        };
    }


}
