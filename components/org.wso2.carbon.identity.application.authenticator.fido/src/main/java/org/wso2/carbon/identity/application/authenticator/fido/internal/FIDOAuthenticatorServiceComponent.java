/*
 * Copyright (c) 2015, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.application.authenticator.fido.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentContext;
import org.wso2.carbon.identity.application.authentication.framework.ApplicationAuthenticator;
import org.wso2.carbon.identity.application.authenticator.fido.FIDOAuthenticator;
import org.wso2.carbon.identity.application.authenticator.fido.u2f.U2FService;
import org.wso2.carbon.identity.user.store.configuration.listener.UserStoreConfigListener;
import org.wso2.carbon.user.core.service.RealmService;

/**
 * @scr.component name="identity.application.authenticator.fido.component" immediate="true"
 * @scr.reference name="realm.service"
 * interface="org.wso2.carbon.user.core.service.RealmService"cardinality="1..1"
 * policy="dynamic" bind="setRealmService" unbind="unsetRealmService"
 */
public class FIDOAuthenticatorServiceComponent {

    private static final Log log = LogFactory.getLog(FIDOAuthenticatorServiceComponent.class);

    private static RealmService realmService;

    protected void activate(ComponentContext context) {
        BundleContext bundleContext = context.getBundleContext();
        FIDOAuthenticator fidoAuthenticator = FIDOAuthenticator.getInstance();

        try {
            bundleContext.registerService(ApplicationAuthenticator.class.getName(), fidoAuthenticator, null);
            if (log.isDebugEnabled()) {
                log.debug("FIDOAuthenticator service is registered");
            }
        } catch (Exception e) {
            log.error("Error registering FIDOAuthenticator service", e);
        }

        U2FService u2FService = U2FService.getInstance();
        try {
            bundleContext.registerService(U2FService.class, u2FService, null);
            if (log.isDebugEnabled()) {
                log.debug("U2FService is registered");
            }
        } catch (Exception e) {
            log.error("Error registering U2FService ", e);
        }

        try {
            bundleContext.registerService(UserStoreConfigListener.class.getName(), new UserStoreConfigListenerImpl(), null);
        } catch (Exception e){
            log.error("Error registering UserStoreConfigListener ", e);
        }

    }

    protected void deactivate(ComponentContext context) {
        if (log.isDebugEnabled()) {
            log.debug("Deactivating FIDOAuthenticator bundle...");
        }
    }

    protected void setRealmService(RealmService realmService) {
        if (log.isDebugEnabled()) {
            log.debug("Setting the Realm Service");
        }
        FIDOAuthenticatorServiceComponent.realmService = realmService;
    }

    protected void unsetRealmService(RealmService realmService) {
        if (log.isDebugEnabled()) {
            log.debug("UnSetting the Realm Service");
        }
        FIDOAuthenticatorServiceComponent.realmService = null;
    }

    public static RealmService getRealmService() {
        return realmService;
    }
}