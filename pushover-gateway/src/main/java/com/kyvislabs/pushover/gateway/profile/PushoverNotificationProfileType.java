package com.kyvislabs.pushover.gateway.profile;

import com.inductiveautomation.ignition.alarming.notification.AlarmNotificationProfile;
import com.inductiveautomation.ignition.alarming.notification.AlarmNotificationProfileRecord;
import com.inductiveautomation.ignition.alarming.notification.AlarmNotificationProfileType;
import com.inductiveautomation.ignition.common.i18n.LocalizedString;
import com.inductiveautomation.ignition.common.user.ContactType;
import com.inductiveautomation.ignition.gateway.localdb.persistence.PersistentRecord;
import com.inductiveautomation.ignition.gateway.localdb.persistence.RecordMeta;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;

public class PushoverNotificationProfileType extends AlarmNotificationProfileType {
    public static final String TYPE_ID = "PushoverType";
    public static final ContactType PUSHOVER =
            new ContactType("Pushover", new LocalizedString("PushoverNotification.ContactType.Pushover"));

    public PushoverNotificationProfileType() {
        super(TYPE_ID,
                "PushoverNotification." + "PushoverNotificationProfileType.Name",
                "PushoverNotification." + "PushoverNotificationProfileType.Description");
    }

    @Override
    public RecordMeta<? extends PersistentRecord> getSettingsRecordType() {
        return PushoverNotificationProfileSettings.META;
    }

    @Override
    public AlarmNotificationProfile createNewProfile(GatewayContext context,
                                                     AlarmNotificationProfileRecord profileRecord) throws Exception {
        PushoverNotificationProfileSettings settings = findProfileSettingsRecord(context, profileRecord);

        if (settings == null) {
            throw new Exception(
                    String.format("Couldn't find settings record for profile '%s'.", profileRecord.getName()));
        }

        return new PushoverNotificationProfile(context, profileRecord, settings);
    }

}
