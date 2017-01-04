package org.itracker.web.actions.admin.user;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.itracker.model.User;
import org.itracker.model.util.UserUtilities;
import org.itracker.services.UserService;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.ptos.UserPTO;
import org.itracker.web.util.ServletContextUtils;
import org.itracker.web.util.SessionManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public class ListUsersAction extends ItrackerBaseAction {


    public ActionForward execute(ActionMapping mapping, ActionForm form,
                                 HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        UserService userService = ServletContextUtils.getItrackerServices().getUserService();
        boolean allowProfileCreation =
                userService.allowProfileCreation(null, null, UserUtilities.AUTH_TYPE_UNKNOWN, UserUtilities.REQ_SOURCE_WEB);
        request.setAttribute("allowProfileCreation", allowProfileCreation);

        int activeSessions = SessionManager.getNumActiveSessions();
        request.setAttribute("activeSessions", activeSessions);

        List<User> users = userService.getAllUsers();
        Collections.sort(users, User.NAME_COMPARATOR);

        List<UserPTO> userDTOList = new LinkedList<UserPTO>();
        for (User user : users) {
            UserPTO aUserDTO = new UserPTO(user, SessionManager.getSessionLastAccess(user.getLogin()));
            userDTOList.add(aUserDTO);
        }

        request.setAttribute("users", userDTOList);

        String pageTitleKey = "itracker.web.admin.listusers.title";
        String pageTitleArg = "";
        request.setAttribute("pageTitleKey", pageTitleKey);
        request.setAttribute("pageTitleArg", pageTitleArg);

        return mapping.findForward("listusers");
    }


}
