package org.itracker.web.actions.admin.configuration;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.itracker.model.Configuration;
import org.itracker.model.CustomField;
import org.itracker.model.util.SystemConfigurationUtilities;
import org.itracker.services.ConfigurationService;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.util.LoginUtilities;
import org.itracker.web.util.ServletContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


public class ListConfigurationAction extends ItrackerBaseAction {


    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        ConfigurationService configurationService = ServletContextUtils.getItrackerServices().getConfigurationService();

        List<Configuration> resolutions = configurationService.getConfigurationItemsByType(Configuration.Type.resolution);
        List<Configuration> severities = configurationService.getConfigurationItemsByType(Configuration.Type.severity);
        List<Configuration> statuses = configurationService.getConfigurationItemsByType(Configuration.Type.status);
        List<CustomField> customfields = configurationService.getCustomFields();

        request.setAttribute("resolutions", resolutions);
        request.setAttribute("severities", severities);
        request.setAttribute("statuses", statuses);
        request.setAttribute("customfields", customfields);
        request.setAttribute("pageLocale", LoginUtilities.getCurrentLocale(request));

        request.setAttribute("pageTitleKey", "itracker.web.admin.listconfiguration.title");
        request.setAttribute("pageTitleArg", "");

        return mapping.findForward("listconfiguration");
    }
}
