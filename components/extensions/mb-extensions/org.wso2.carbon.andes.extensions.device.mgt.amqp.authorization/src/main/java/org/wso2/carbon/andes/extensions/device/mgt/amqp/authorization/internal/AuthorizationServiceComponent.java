package org.wso2.carbon.andes.extensions.device.mgt.amqp.authorization.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.wso2.carbon.andes.extensions.device.mgt.amqp.authorization.config.AuthorizationConfiguration;
import org.wso2.carbon.andes.extensions.device.mgt.amqp.authorization.config.AuthorizationConfigurationManager;
import org.wso2.carbon.user.core.service.RealmService;

/**
 * @scr.component name="org.wso2.carbon.devicemgt.policy.manager" immediate="true"
 * @scr.reference name="user.realmservice.default"
 * interface="org.wso2.carbon.user.core.service.RealmService"
 * cardinality="1..1"
 * policy="dynamic"
 * bind="setRealmService"
 * unbind="unsetRealmService"
 */
@SuppressWarnings("unused")
public class AuthorizationServiceComponent {

    private static Log log = LogFactory.getLog(AuthorizationServiceComponent.class);

    protected void activate(ComponentContext componentContext) {
        try {
            AuthorizationConfiguration.initialize();
            AuthorizationConfigurationManager.getInstance().initConfig();
        } catch (Throwable e) {
            log.error("Failed to activate org.wso2.carbon.andes.authorization.internal." +
                    "AuthorizationServiceComponent : " + e);
        }
    }

    @SuppressWarnings("unused")
    protected void deactivate(ComponentContext componentContext) {

    }

    /**
     * Sets Realm Service
     *
     * @param realmService An instance of RealmService
     */
    protected void setRealmService(RealmService realmService) {
        if (log.isDebugEnabled()) {
            log.debug("Setting Realm Service");
        }
        AuthorizationDataHolder.getInstance().setRealmService(realmService);
    }

    /**
     * Unsets Realm Service
     *
     * @param realmService An instance of RealmService
     */
    protected void unsetRealmService(RealmService realmService) {
        if (log.isDebugEnabled()) {
            log.debug("Unsetting Realm Service");
        }
        AuthorizationDataHolder.getInstance().setRealmService(null);
    }
}
