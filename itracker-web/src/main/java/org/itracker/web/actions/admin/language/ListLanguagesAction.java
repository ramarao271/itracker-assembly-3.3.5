package org.itracker.web.actions.admin.language;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.itracker.core.resources.ITrackerResources;
import org.itracker.services.ConfigurationService;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.util.ServletContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public class ListLanguagesAction extends ItrackerBaseAction {

    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        ConfigurationService configurationService = ServletContextUtils.getItrackerServices()
                .getConfigurationService();

        Map<String, List<String>> languages = configurationService.getAvailableLanguages();


        String baseLocaleName = ITrackerResources.getString("itracker.web.attr.baselocale");
        request.setAttribute("languages", languages);
        request.setAttribute("languageKeys", languages.keySet().toArray());
        request.setAttribute("baseLocaleName", baseLocaleName);
        request.setAttribute("baseLocale", ITrackerResources.BASE_LOCALE);

        return mapping.findForward("listlanguages");

    }
}
