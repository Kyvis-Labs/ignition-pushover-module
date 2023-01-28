package com.kyvislabs.pushover.gateway;

import com.inductiveautomation.ignition.alarming.AlarmNotificationContext;
import com.inductiveautomation.ignition.alarming.common.ModuleMeta;
import com.inductiveautomation.ignition.common.BundleUtil;
import com.inductiveautomation.ignition.common.licensing.LicenseState;
import com.inductiveautomation.ignition.common.script.ScriptManager;
import com.inductiveautomation.ignition.common.script.hints.PropertiesFileDocProvider;
import com.inductiveautomation.ignition.gateway.clientcomm.ClientReqSession;
import com.inductiveautomation.ignition.gateway.model.AbstractGatewayModuleHook;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.services.ModuleServiceConsumer;
import com.kyvislabs.pushover.common.scripting.PushoverClientImpl;
import com.kyvislabs.pushover.gateway.profile.PushoverNotificationProfileSettings;
import com.kyvislabs.pushover.gateway.profile.PushoverNotificationProfileType;
import com.kyvislabs.pushover.gateway.profile.PushoverProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GatewayHook extends AbstractGatewayModuleHook implements ModuleServiceConsumer {
    public static final String MODULE_ID = "com.kyvislabs.pushover";
    private final Logger logger = LoggerFactory.getLogger("Pushover.Gateway.Hook");

    private GatewayContext gatewayContext;
    private volatile AlarmNotificationContext notificationContext;
    private final PushoverClientImpl pushoverClient = new PushoverClientImpl();


    @Override
    public void setup(GatewayContext gatewayContext) {
        this.gatewayContext = gatewayContext;
        BundleUtil.get().addBundle("PushoverNotification", getClass(), "PushoverNotification");

        gatewayContext.getModuleServicesManager().subscribe(AlarmNotificationContext.class, this);

        gatewayContext.getAlarmManager()
                .registerExtendedConfigProperties(ModuleMeta.MODULE_ID, PushoverProperties.CUSTOM_MESSAGE);

        gatewayContext.getUserSourceManager().registerContactType(PushoverNotificationProfileType.PUSHOVER);

        try {
            gatewayContext.getSchemaUpdater().updatePersistentRecords(PushoverNotificationProfileSettings.META);
        } catch (Exception e) {
            logger.error("Error configuring internal database", e);
        }
    }

    @Override
    public void notifyLicenseStateChanged(LicenseState licenseState) {

    }

    @Override
    public void startup(LicenseState licenseState) {

    }

    @Override
    public void shutdown() {
        gatewayContext.getModuleServicesManager().unsubscribe(AlarmNotificationContext.class, this);

        if (notificationContext != null) {
            try {
                notificationContext.getAlarmNotificationManager().removeAlarmNotificationProfileType(
                        new PushoverNotificationProfileType());
            } catch (Exception e) {
                logger.error("Error removing notification profile.", e);
            }
        }

        BundleUtil.get().removeBundle("PushoverNotification");
    }

    @Override
    public void serviceReady(Class<?> serviceClass) {
        if (serviceClass == AlarmNotificationContext.class) {
            notificationContext = gatewayContext.getModuleServicesManager()
                    .getService(AlarmNotificationContext.class);

            try {
                notificationContext.getAlarmNotificationManager().addAlarmNotificationProfileType(
                        new PushoverNotificationProfileType());
            } catch (Exception e) {
                logger.error("Error adding notification profile.", e);
            }
        }
    }

    @Override
    public void serviceShutdown(Class<?> arg0) {
        notificationContext = null;
    }

    @Override
    public boolean isMakerEditionCompatible() {
        return true;
    }

    @Override
    public boolean isFreeModule() {
        return true;
    }

    @Override
    public void initializeScriptManager(ScriptManager manager) {
        super.initializeScriptManager(manager);
        manager.addScriptModule("system.pushover", pushoverClient, new PropertiesFileDocProvider());
    }

    @Override
    public Object getRPCHandler(ClientReqSession session, String projectName) {
        return pushoverClient;
    }
}
