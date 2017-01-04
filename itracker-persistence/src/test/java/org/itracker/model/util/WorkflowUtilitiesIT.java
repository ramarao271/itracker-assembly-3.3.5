/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.itracker.model.util;

import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.WorkflowException;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.model.*;
import org.junit.Ignore;
import org.junit.Test;

import java.util.*;
import static org.itracker.Assert.*;

/**
 * @author seas
 */
public class WorkflowUtilitiesIT extends AbstractDependencyInjectionTest {

    /**
     * Verifies WorkflowUtilities.getEventName
     */
    @Test
    public void testGetEventName() {
        // testing a case of missing key
        doTestGetEventName(null,
                999, "MISSING KEY: itracker.workflow.field.event.999");

        // "On Populate"
        doTestGetEventName(new Locale(ITrackerResources.BASE_LOCALE),
                WorkflowUtilities.EVENT_FIELD_ONPOPULATE, "On Populate");
        doTestGetEventName(null,
                WorkflowUtilities.EVENT_FIELD_ONPOPULATE, "On Populate");
        doTestGetEventName(new Locale("test"),
                WorkflowUtilities.EVENT_FIELD_ONPOPULATE, "test_value");

        // "On Sort"
        doTestGetEventName(new Locale(ITrackerResources.BASE_LOCALE),
                WorkflowUtilities.EVENT_FIELD_ONSORT, "On Sort");
        doTestGetEventName(null,
                WorkflowUtilities.EVENT_FIELD_ONSORT, "On Sort");
        doTestGetEventName(new Locale("test"),
                WorkflowUtilities.EVENT_FIELD_ONSORT, "On Sort");

        // "On SetDefault"
        doTestGetEventName(new Locale(ITrackerResources.BASE_LOCALE),
                WorkflowUtilities.EVENT_FIELD_ONSETDEFAULT, "On Set Default");
        doTestGetEventName(null,
                WorkflowUtilities.EVENT_FIELD_ONSETDEFAULT, "On Set Default");
        doTestGetEventName(new Locale("test"),
                WorkflowUtilities.EVENT_FIELD_ONSETDEFAULT, "On Set Default");

        // "On Validate"
        doTestGetEventName(new Locale(ITrackerResources.BASE_LOCALE),
                WorkflowUtilities.EVENT_FIELD_ONVALIDATE, "On Validate");
        doTestGetEventName(null,
                WorkflowUtilities.EVENT_FIELD_ONVALIDATE, "On Validate");
        doTestGetEventName(new Locale("test"),
                WorkflowUtilities.EVENT_FIELD_ONVALIDATE, "On Validate");

        // "On PreSubmit"
        doTestGetEventName(new Locale(ITrackerResources.BASE_LOCALE),
                WorkflowUtilities.EVENT_FIELD_ONPRESUBMIT, "On Pre Submit");
        doTestGetEventName(null,
                WorkflowUtilities.EVENT_FIELD_ONPRESUBMIT, "On Pre Submit");
        doTestGetEventName(new Locale("test"),
                WorkflowUtilities.EVENT_FIELD_ONPRESUBMIT, "On Pre Submit");

        // "On PostSubmit"
        doTestGetEventName(new Locale(ITrackerResources.BASE_LOCALE),
                WorkflowUtilities.EVENT_FIELD_ONPOSTSUBMIT, "On Post Submit");
        doTestGetEventName(null,
                WorkflowUtilities.EVENT_FIELD_ONPOSTSUBMIT, "On Post Submit");
        doTestGetEventName(new Locale("test"),
                WorkflowUtilities.EVENT_FIELD_ONPOSTSUBMIT, "On Post Submit");
    }

    /**
     * Verifies WorkflowUtilities.getEvents
     */
    @Test
    public void testGetEvents() {
        doTestGetEvents(null,
                new NameValuePair[]{
                        new NameValuePair("On Populate",
                                Integer.toString(WorkflowUtilities.EVENT_FIELD_ONPOPULATE)),
//                        new NameValuePair("On Sort",
//                                Integer.toString(WorkflowUtilities.EVENT_FIELD_ONSORT)),
                        new NameValuePair("On Set Default",
                                Integer.toString(WorkflowUtilities.EVENT_FIELD_ONSETDEFAULT)),
                        new NameValuePair("On Validate",
                                Integer.toString(WorkflowUtilities.EVENT_FIELD_ONVALIDATE)),
                        new NameValuePair("On Pre Submit",
                                Integer.toString(WorkflowUtilities.EVENT_FIELD_ONPRESUBMIT)),
                        new NameValuePair("On Post Submit",
                                Integer.toString(WorkflowUtilities.EVENT_FIELD_ONPOSTSUBMIT))
                });
        doTestGetEvents(new Locale("en_US"),
                new NameValuePair[]{
                        new NameValuePair("On Populate",
                                Integer.toString(WorkflowUtilities.EVENT_FIELD_ONPOPULATE)),
//                        new NameValuePair("On Sort",
//                                Integer.toString(WorkflowUtilities.EVENT_FIELD_ONSORT)),
                        new NameValuePair("On Set Default",
                                Integer.toString(WorkflowUtilities.EVENT_FIELD_ONSETDEFAULT)),
                        new NameValuePair("On Validate",
                                Integer.toString(WorkflowUtilities.EVENT_FIELD_ONVALIDATE)),
                        new NameValuePair("On Pre Submit",
                                Integer.toString(WorkflowUtilities.EVENT_FIELD_ONPRESUBMIT)),
                        new NameValuePair("On Post Submit",
                                Integer.toString(WorkflowUtilities.EVENT_FIELD_ONPOSTSUBMIT))
                });
        doTestGetEvents(new Locale("test"),
                new NameValuePair[]{
                        new NameValuePair("test_value",
                                Integer.toString(WorkflowUtilities.EVENT_FIELD_ONPOPULATE)),
//                        new NameValuePair("On Sort",
//                                Integer.toString(WorkflowUtilities.EVENT_FIELD_ONSORT)),
                        new NameValuePair("On Set Default",
                                Integer.toString(WorkflowUtilities.EVENT_FIELD_ONSETDEFAULT)),
                        new NameValuePair("On Validate",
                                Integer.toString(WorkflowUtilities.EVENT_FIELD_ONVALIDATE)),
                        new NameValuePair("On Pre Submit",
                                Integer.toString(WorkflowUtilities.EVENT_FIELD_ONPRESUBMIT)),
                        new NameValuePair("On Post Submit",
                                Integer.toString(WorkflowUtilities.EVENT_FIELD_ONPOSTSUBMIT))
                });
    }

    /**
     * Verifies WorkflowUtilities.getListOptions
     */
    @Test
    public void testGetListOptions() {
        final List<NameValuePair> testedList = new Vector<NameValuePair>();
        testedList.add(new NameValuePair("On Populate",
                Integer.toString(WorkflowUtilities.EVENT_FIELD_ONPOPULATE)));
        testedList.add(new NameValuePair("On Sort",
                Integer.toString(WorkflowUtilities.EVENT_FIELD_ONSORT)));
        testedList.add(new NameValuePair("On SetDefault",
                Integer.toString(WorkflowUtilities.EVENT_FIELD_ONSETDEFAULT)));
        testedList.add(new NameValuePair("On Validate",
                Integer.toString(WorkflowUtilities.EVENT_FIELD_ONVALIDATE)));
        testedList.add(new NameValuePair("On PreSubmit",
                Integer.toString(WorkflowUtilities.EVENT_FIELD_ONPRESUBMIT)));
        testedList.add(new NameValuePair("On PostSubmit",
                Integer.toString(WorkflowUtilities.EVENT_FIELD_ONPOSTSUBMIT)));
        final Map<Integer, List<NameValuePair>> testedListOptions =
                new HashMap<Integer, List<NameValuePair>>();
        testedListOptions.put(888, testedList);
        assertTrue(testedList == WorkflowUtilities.getListOptions(testedListOptions, 888));
        assertTrue(0 == WorkflowUtilities.getListOptions(testedListOptions, 999).size());
    }

    /**
     * Verifies WorkflowUtilities.processFieldScripts:
     * 1. Verifies that no scripts are run when no matches with incoming event.
     * 2. Verifies that no scripts are run when no matches with updated field.
     * 3. Verifies that script is run when everything matches well and proper
     * result is returned, but validatorForm is not updated since incoming
     * event doesn't suppose this.
     */
    @Test
    public void testProcessFieldScripts() {
        final List<ProjectScript> projectScripts = new Vector<ProjectScript>();
        final Project project = new Project("testProcessFieldScript");
        {
            final ProjectScript projectScript = new ProjectScript();
            projectScript.setProject(project);
            final WorkflowScript workflowScript = new WorkflowScript();
            workflowScript.setEvent(WorkflowUtilities.EVENT_FIELD_ONPOSTSUBMIT);
            workflowScript.setScript(
                    "import org.itracker.model.NameValuePair;\n" +
                            "optionValues.add(" +
                            "new NameValuePair(\"fieldDescription\", \"onPostSubmit\"));\n");
            projectScript.setScript(workflowScript);
            projectScript.setFieldId(IssueUtilities.FIELD_STATUS);
            projectScript.setFieldType(Configuration.Type.customfield);
            projectScript.setPriority(1);
            projectScripts.add(projectScript);
        }
        {
            final ProjectScript projectScript = new ProjectScript();
            projectScript.setProject(project);
            final WorkflowScript workflowScript = new WorkflowScript();
            workflowScript.setEvent(WorkflowUtilities.EVENT_FIELD_ONSETDEFAULT);
            workflowScript.setScript(
                    "import org.itracker.model.NameValuePair;\n" +
                            "optionValues.add(" +
                            "new NameValuePair(\"status\", \"onSetDefault\"));\n");
            projectScript.setScript(workflowScript);
            projectScript.setFieldId(IssueUtilities.FIELD_STATUS);
            projectScript.setFieldType(Configuration.Type.customfield);
            projectScript.setPriority(1);
            projectScripts.add(projectScript);
        }
//      todo web test
//        class ValidatorFormAdhoc extends IssueForm {
//
//            /**
//             *
//             */
//            private static final long serialVersionUID = 1L;
//            public String status = "defaultStatus";
//            public String fieldDescription = "defaultDescription";
//        }
//
//        try {
//            final ValidatorFormAdhoc validatorForm = new ValidatorFormAdhoc();
//            final Map<Integer, List<NameValuePair>> possibleValues =
//                    new HashMap<Integer, List<NameValuePair>>();
//
//            possibleValues.put(IssueUtilities.FIELD_STATUS,
//                    new Vector<NameValuePair>());
//
//            Map<Integer, String> currentValues = new HashMap<Integer, String>(1);
//            currentValues.put(IssueUtilities.FIELD_STATUS, validatorForm.status);
//
//            validatorForm.processFieldScripts(projectScripts,
//                    WorkflowUtilities.EVENT_FIELD_ONPOSTSUBMIT,
//                    currentValues,
//                    possibleValues,
//                    new ActionMessages());
//            assertEquals("status possibleValues.size", 1,
//                    possibleValues.get(IssueUtilities.FIELD_STATUS).size());
//            assertEquals("possibleValues possibleValues[0].name", "fieldDescription",
//                    possibleValues.get(IssueUtilities.FIELD_STATUS).get(0).getName());
//            assertEquals("possibleValues possibleValues[0].value", "onPostSubmit",
//                    possibleValues.get(IssueUtilities.FIELD_STATUS).get(0).getValue());
//            assertEquals("validatorForm.fieldDescription", "defaultDescription", validatorForm.fieldDescription);
//            assertEquals("validatorForm.status", "defaultStatus", validatorForm.status);
//        } catch (final WorkflowException e) {
//            assertTrue(e.getMessage(), false);
//        }

//        // There are problems with logic of WorkflowUtilities.setFormProperty
//        try {
//            final ValidatorFormAdhoc validatorForm = new ValidatorFormAdhoc();
//            final List<NameValuePair> result =
//                    WorkflowUtilities.processFieldScripts(projectScripts,
//                    WorkflowUtilities.EVENT_FIELD_ONSETDEFAULT,
//                    IssueUtilities.FIELD_STATUS,
//                    new Vector<NameValuePair>() /* currentValue */,
//                    new ActionErrors() /* currentErrors */,
//                    validatorForm);
//            assertEquals(1, result.size());
//            assertEquals("status", result.get(0).getName());
//            assertEquals("onSetDefault", result.get(0).getValue());
//            assertEquals("defaultDescription", validatorForm.defaultDescription);
//            assertEquals("onSetDefault", validatorForm.status);
//        } catch (final WorkflowException e) {        Set
//            //
//        }
//        // There are problems with logic of WorkflowUtilities.setFormProperty        
//        try {
//            final ValidatorFormAdhoc validatorForm = new ValidatorFormAdhoc();
//            final List<NameValuePair> result =
//                    WorkflowUtilities.processFieldScripts(projectScripts,
//                    WorkflowUtilities.EVENT_FIELD_ONSETDEFAULT,
//                    1 /* custom field */,
//                    new Vector<NameValuePair>() /* currentValue */,
//                    new ActionErrors() /* currentErrors */,
//                    validatorForm);
//            assertEquals(1, result.size());
//            assertEquals("status", result.get(0).getName());
//            assertEquals("onSetDefault", result.get(0).getValue());
//            assertEquals("defaultDescription", validatorForm.defaultDescription);
//            assertEquals("onSetDefault", validatorForm.status);
//        } catch (final WorkflowException e) {
//            //
//        }

    }

    private void doTestGetEvents(final Locale locale,
                                 final NameValuePair[] expected) {
        final NameValuePair[] actual = WorkflowUtilities.getEvents(locale);
        for (final NameValuePair nvpExpected : expected) {
            boolean found = false;
            for (final NameValuePair nvpActual : actual) {
                found = nvpExpected.getName().equals(nvpActual.getName())
                        && nvpExpected.getValue().equals(nvpActual.getValue());
                if (found) break;
            }
            assertTrue("WorkflowUtilities.getEvents(" + locale + ").contains(" +
                    "new NameValuePair(" + nvpExpected.getName() + "," +
                    nvpExpected.getValue() + "))",
                    found);
        }
        assertEquals("WorkflowUtilities.getEvents(" + locale + ").length",
                expected.length, actual.length);
    }

    private void doTestGetEventName(final Locale locale,
                                    final int eventId, final String expected) {
        final String actual =
                WorkflowUtilities.getEventName(eventId, locale);
        assertEquals("WorkflowUtilities.getEventName(" + eventId + ", " +
                locale + ")", expected, actual);
    }

    /**
     * Defines a set of datafiles to be uploaded into database.
     *
     * @return an array with datafiles.
     */
    protected String[] getDataSetFiles() {
        return new String[]{
                "dataset/languagebean_dataset.xml"
        };
    }

    /**
     * Defines a simple configuration, required for running tests.
     *
     * @return an array of references to configuration files.
     */
    protected String[] getConfigLocations() {
        return new String[]{"application-context.xml"};
    }
}
