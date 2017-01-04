package org.itracker.web.scheduler.tasks;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;

/**
 * This class implements the periodic transmission of the meals and reservations
 * file.
 */
public abstract class BaseJob implements StatefulJob {

    protected Object getServices(JobExecutionContext context) {
        return context.getJobDetail().getJobDataMap().get("services");
    }

    public void execute(final JobExecutionContext context) throws JobExecutionException {
    }

}