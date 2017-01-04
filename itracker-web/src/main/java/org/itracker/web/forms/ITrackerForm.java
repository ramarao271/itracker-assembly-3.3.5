package org.itracker.web.forms;

import org.apache.struts.validator.ValidatorForm;
import org.itracker.services.ITrackerServices;
import org.itracker.web.util.ServletContextUtils;

/**
 * This form is by the struts actions to pass issue data.
 */
public abstract class ITrackerForm extends ValidatorForm {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    protected ITrackerServices getITrackerServices() {
        return ServletContextUtils.getItrackerServices();
    }

}
