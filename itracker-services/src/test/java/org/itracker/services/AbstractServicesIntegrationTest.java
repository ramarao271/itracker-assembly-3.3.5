package org.itracker.services;


import org.itracker.AbstractDependencyInjectionTest;
import org.itracker.core.resources.ITrackerResources;

public abstract class AbstractServicesIntegrationTest extends AbstractDependencyInjectionTest {

    @Override
    protected void resetConfiguration() {
        synchronized (ITrackerResources.class) {
            if (ITrackerResources.isInitialized()) {
                return;
            }
            ConfigurationService configurationService = (ConfigurationService) applicationContext
                    .getBean("configurationService");

            ITrackerResources.setConfigurationService(configurationService);

            configurationService.initializeAllLanguages(true);
            configurationService.initializeConfiguration();

            configurationService.resetConfigurationCache();

        }

    }

}