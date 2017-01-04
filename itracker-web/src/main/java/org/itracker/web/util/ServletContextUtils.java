package org.itracker.web.util;

import org.itracker.services.ITrackerServices;

/**
 * This class gets access to ITracker services layer from a servlet context
 * Since actions already have it done in the top level ITrackerAction (ItrackerBaseAction), this is mostly
 * to be used in JSPs.
 * <p/>
 * Marky: And that means that this is somehow deprecated because we
 * want to move all logic code except Taglibs from the JSPs to the Actions.
 * <p/>
 * TODO In fact this shouldn't be needed / used, ITracker's JSP have way too much java code
 *
 * @author ricardo
 */
public class ServletContextUtils {

    private static ITrackerServices itrackerServices;

    public static void setITrackerServices(ITrackerServices services) {
        itrackerServices = services;
    }

    public static ITrackerServices getItrackerServices() {
        return itrackerServices;
    }

}

