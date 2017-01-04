package org.itracker.web.struts.mock;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class MockActionMapping extends ActionMapping {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private ActionForward actionForward;

    public ActionForward findForward(String name) {

        actionForward = new ActionForward(name);

        return actionForward;
    }

    public ActionForward getActionForward() {
        return actionForward;
    }

}
