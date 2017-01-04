package org.itracker.web.util;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.itracker.model.Component;

import javax.servlet.http.HttpServletRequest;

public class EditComponentFormActionUtil {
    private static final Logger log = Logger.getLogger(EditComponentFormActionUtil.class);

    public static ActionForward init(ActionMapping mapping, HttpServletRequest request) {
        log.debug("Getting component from session");
        final Component component = (Component) request.getSession().getAttribute(Constants.COMPONENT_KEY);
        log.debug("Checking if component is new");
        final boolean isNew = component.isNew();
        log.debug("Putting component" + component.getName() + " and isNew back into the request");
        request.setAttribute("component", component);
        request.setAttribute("isNew", isNew);
        return null;
    }

}
