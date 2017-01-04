package org.itracker.web.actions;

import org.apache.struts.action.*;
import org.itracker.web.actions.base.ItrackerBaseAction;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class UnauthorizedAction extends ItrackerBaseAction {

    private final static String UNAUTHORIZED = "unauthorized";

    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        ActionErrors messages = new ActionErrors();
        messages.add(ActionMessages.GLOBAL_MESSAGE,
                new ActionMessage("itracker.web.error.unauthorized"));
        saveErrors(request, messages);
        return mapping.findForward(UNAUTHORIZED);
    }

}
