package org.itracker.web.actions.admin.report;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.apache.struts.action.*;
import org.itracker.model.PermissionType;
import org.itracker.model.Report;
import org.itracker.model.util.UserUtilities;
import org.itracker.services.ReportService;
import org.itracker.web.actions.base.ItrackerBaseAction;
import org.itracker.web.forms.ReportForm;
import org.itracker.web.util.Constants;
import org.itracker.web.util.RequestHelper;
import org.itracker.web.util.ServletContextUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Set;

//TODO: Action Cleanup

public class EditReportFormAction extends ItrackerBaseAction {
    private static final Logger log = Logger.getLogger(EditReportFormAction.class);

    public EditReportFormAction() {
    }

    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ActionMessages errors = new ActionMessages();

        try {
            ReportService reportService = ServletContextUtils.getItrackerServices().getReportService();

            HttpSession session = request.getSession(true);
            String action = request.getParameter("action");

            Report report;

            report = null;

            ReportForm reportForm = (ReportForm) form;
            if (reportForm == null) {
                reportForm = new ReportForm();
            }


            if ("create".equals(action)) {
                report = new Report();
                report.setId(-1);
                reportForm.setAction("create");
                reportForm.setId(report.getId());
            } else if ("update".equals(action)) {
                Integer reportId = (Integer) PropertyUtils.getSimpleProperty(form, "id");
                report = reportService.getReportDAO().findByPrimaryKey(reportId);
                if (report == null) {
                    errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidreport"));
                } else {
                    reportForm.setAction("update");
                    reportForm.setId(report.getId());
                    reportForm.setName(report.getName());
                    reportForm.setNameKey(report.getNameKey());
                    reportForm.setDescription(report.getDescription());
                    reportForm.setClassName(report.getClassName());
                    if (null != report.getFileData()) {
                        String fileDataString = getAsString(report.getFileData(), errors);
                        reportForm.setFileData(fileDataString);
                    }

                }
            } else {
                errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.invalidaction"));
            }

            if (errors.isEmpty()) {
                request.setAttribute("reportForm", reportForm);
                session.setAttribute(Constants.REPORT_KEY, report);
                saveToken(request);
                return mapping.getInputForward();
            }
        } catch (RuntimeException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            log.error("Exception while creating edit report form.", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        }

        if (!errors.isEmpty()) {
            saveErrors(request, errors);
        }

        return mapping.findForward("error");
    }

    public static String getAsString(byte[] xmlBytes, ActionMessages errors) {

        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
                    new ByteArrayInputStream(xmlBytes));
            StringWriter w = new StringWriter();

            Transformer t = TransformerFactory.newInstance().newTransformer();
            t.transform(new DOMSource(doc), new StreamResult(w));

            return w.toString();

        } catch (SAXException e) {
            log.debug("Exception while creating edit report form.", e);
        } catch (ParserConfigurationException | TransformerException | IOException e) {
            log.error("Exception while creating edit report form.", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        }
        return null;
    }

    public static byte[] getAsXmlBytes (String xmlString, ActionMessages errors) {

        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
                    new ByteArrayInputStream(xmlString.getBytes("utf-8")));
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            OutputStreamWriter w = new OutputStreamWriter(os, "utf-8");

            Transformer t = TransformerFactory.newInstance().newTransformer();
            t.transform(new DOMSource(doc), new StreamResult(w));

            return os.toByteArray();

        } catch (SAXException | ParserConfigurationException e) {
            log.debug("Exception while creating edit report form.", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.details", e.getMessage()));
        } catch (TransformerException | IOException e) {
            log.error("Exception while creating edit report form.", e);
            errors.add(ActionMessages.GLOBAL_MESSAGE, new ActionMessage("itracker.web.error.system"));
        }
        return null;
    }

}
  