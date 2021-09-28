package com.kyvislabs.pushover.gateway.profile;

import com.inductiveautomation.ignition.alarming.notification.AlarmNotificationProfileRecord;
import com.inductiveautomation.ignition.gateway.audit.AuditProfileRecord;
import com.inductiveautomation.ignition.gateway.localdb.persistence.*;
import simpleorm.dataset.SFieldFlags;

public class PushoverNotificationProfileSettings extends PersistentRecord {

    public static final RecordMeta<PushoverNotificationProfileSettings> META =
            new RecordMeta<>(
                    PushoverNotificationProfileSettings.class,
                    "PushoverNotificationProfileSettings"
            );
    public static final IdentityField Id = new IdentityField(META);
    public static final LongField ProfileId = new LongField(META, "ProfileId");
    public static final ReferenceField<AlarmNotificationProfileRecord> Profile = new ReferenceField<>(
            META,
            AlarmNotificationProfileRecord.META,
            "Profile",
            ProfileId);

    public static final StringField APIToken = new StringField(META, "APIToken", SFieldFlags.SMANDATORY);
    public static final StringField UserKey = new StringField(META, "UserKey", SFieldFlags.SMANDATORY);

    static final Category API = new Category("PushoverNotificationProfileSettings.Category.API", 1)
            .include(APIToken, UserKey);

    public static final LongField AuditProfileId = new LongField(META, "AuditProfileId");
    public static final ReferenceField<AuditProfileRecord> AuditProfile = new ReferenceField<>(
            META, AuditProfileRecord.META, "AuditProfile", AuditProfileId);

    static final Category Auditing = new Category("PushoverNotificationProfileSettings.Category.Auditing", 2)
            .include(AuditProfile);

    static {
        Profile.getFormMeta().setVisible(false);
    }

    @Override
    public RecordMeta<?> getMeta() {
        return META;
    }

    public String getAuditProfileName() {
        AuditProfileRecord rec = findReference(AuditProfile);
        return rec == null ? null : rec.getName();
    }

    public String getAPIToken() {
        return getString(APIToken);
    }

    public String getUserKey() {
        return getString(UserKey);
    }
}

