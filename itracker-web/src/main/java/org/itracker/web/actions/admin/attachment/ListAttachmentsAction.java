package org.itracker.web.actions.admin.attachment;

import org.apache.log4j.Logger;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.itracker.services.IssueService;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.ptos.ListAttachmentsPTO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class ListAttachmentsAction extends ItrackerBaseAction {
    private static final String TITLE_KEY = "itracker.web.admin.listattachments.title";
    private static final String LIST_ATTACHMENTS_PAGE = "listattachments";
    private static final Logger log = Logger.getLogger(ListAttachmentsAction.class);

    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response)
            throws Exception {

        IssueService issueService = this.getITrackerServices().getIssueService();
        ListAttachmentsPTO pto = new ListAttachmentsPTO();
        try {
            pto.setAttachments(issueService.getAllIssueAttachments());
        } catch (Exception e) {
            log.error("execute: failed to get all attachments", e);
            throw e;
        }
        request.setAttribute("pageTitleKey", TITLE_KEY);
        request.setAttribute("pageTitleArg", "");
        request.setAttribute("pto", pto);
        return mapping.findForward(LIST_ATTACHMENTS_PAGE);
    }
}
