package org.itracker.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ImportDataModelTest {
    private ImportDataModel idm;

    @Test
    public void testGetData() {
        assertNotNull("data not null", idm.getData());
        AbstractEntity[] entities = new AbstractEntity[1];
        AbstractEntity[] existingModel = new AbstractEntity[1];
        idm.setData(entities, existingModel);
        assertEquals("data length is 1", 1, idm.getData().length);
    }

    @Test
    public void testGetExistingModel() {
        assertNotNull("existingModel not null", idm.getExistingModel());
        AbstractEntity[] entities = new AbstractEntity[1];
        AbstractEntity[] existingModel = new AbstractEntity[1];
        idm.setData(entities, existingModel);
        assertEquals("existingModel length is 1", 1, idm.getExistingModel().length);
    }

    @Test
    public void testGetExistingModelByIndex() {
        assertNotNull("existingModel not null", idm.getExistingModel());
        AbstractEntity[] entities = new AbstractEntity[1];
        AbstractEntity[] existingModel = new AbstractEntity[1];
        existingModel[0] = null;
        idm.setData(entities, existingModel);
        assertEquals("existingModel length is 1", 1, idm.getExistingModel().length);
        assertNull("existingModel index o value", idm.getExistingModel()[0]);
    }


    @Test
    public void testSetData() {
        AbstractEntity[] entities = new AbstractEntity[1];
        AbstractEntity[] existingModel = new AbstractEntity[1];
        idm.setData(entities, existingModel);
        assertEquals("data length is 1", 1, idm.getData().length);

        try {
            idm.setData(null, existingModel);
            fail("did not throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }

        try {
            idm.setData(entities, null);
            fail("did not throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }

        try {
            idm.setData(null, null);
            fail("did not throw IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            // expected
        }
    }

    @Test
    public void testAddVerifyStatistic() {
        idm.addVerifyStatistic(1, 1);
        assertEquals("value is 1", 1, idm.getImportStatistics()[1][1]);
        try {
            idm.addVerifyStatistic(10, 3);
            fail("did not throw RuntimeException");
        } catch (RuntimeException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testStatsToString() {
        idm.addVerifyStatistic(1, 1);
        assertEquals("stats string is ",
                "0:[0, 0] 1:[0, 1] 2:[0, 0] 3:[0, 0] 4:[0, 0] 5:[0, 0] 6:[0, 0] ",
                idm.statsToString());
    }


    @Test
    public void testToString() {
        assertNotNull("toString", idm.toString());

    }


    @Before
    public void setUp() throws Exception {
        idm = new ImportDataModel();
    }

    @After
    public void tearDown() throws Exception {
        idm = null;
    }

}
