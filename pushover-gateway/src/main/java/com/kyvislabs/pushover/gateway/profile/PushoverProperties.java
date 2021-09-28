package com.kyvislabs.pushover.gateway.profile;

import com.inductiveautomation.ignition.alarming.common.notification.BasicNotificationProfileProperty;
import com.inductiveautomation.ignition.common.alarming.config.AlarmProperty;
import com.inductiveautomation.ignition.common.alarming.config.BasicAlarmProperty;
import com.inductiveautomation.ignition.common.config.ConfigurationProperty;
import com.inductiveautomation.ignition.common.i18n.LocalizedString;

import java.util.ArrayList;
import java.util.List;

import static com.inductiveautomation.ignition.common.BundleUtil.i18n;

public class PushoverProperties {

    public static final BasicNotificationProfileProperty<String> MESSAGE = new BasicNotificationProfileProperty<>(
            "message",
            "PushoverNotification." + "Properties.Message.DisplayName",
            null,
            String.class
    );

    public static final BasicNotificationProfileProperty<String> THROTTLED_MESSAGE =
            new BasicNotificationProfileProperty<>(
                    "throttledMessage",
                    "PushoverNotification." + "Properties.ThrottledMessage.DisplayName",
                    null,
                    String.class
            );

    public static final BasicNotificationProfileProperty<Long> TIME_BETWEEN_NOTIFICATIONS =
            new BasicNotificationProfileProperty<>(
                    "delayBetweenContact",
                    "PushoverNotification." + "Properties.TimeBetweenNotifications.DisplayName",
                    null,
                    Long.class
            );

    public static final BasicNotificationProfileProperty<String> TITLE = new BasicNotificationProfileProperty<>(
            "title",
            "PushoverNotification." + "Properties.Title.DisplayName",
            null,
            String.class
    );

    public static final BasicNotificationProfileProperty<Integer> PRIORITY = new BasicNotificationProfileProperty<>(
            "priority",
            "PushoverNotification." + "Properties.Priority.DisplayName",
            null,
            Integer.class
    );

    public static final BasicNotificationProfileProperty<Integer> RETRY = new BasicNotificationProfileProperty<>(
            "retry",
            "PushoverNotification." + "Properties.Retry.DisplayName",
            null,
            Integer.class
    );

    public static final BasicNotificationProfileProperty<Integer> EXPIRE = new BasicNotificationProfileProperty<>(
            "expire",
            "PushoverNotification." + "Properties.Expire.DisplayName",
            null,
            Integer.class
    );

    public static final BasicNotificationProfileProperty<String> SOUND = new BasicNotificationProfileProperty<>(
            "sound",
            "PushoverNotification." + "Properties.Sound.DisplayName",
            null,
            String.class
    );

    public static final BasicNotificationProfileProperty<Boolean> TEST_MODE = new BasicNotificationProfileProperty<>(
            "testMode",
            "PushoverNotification." + "Properties.TestMode.DisplayName",
            null,
            Boolean.class
    );

    /**
     * EXTENDED CONFIG - These are different than the properties above, they are registered for each alarm through the
     * extended config system
     **/
    public static AlarmProperty<String> CUSTOM_MESSAGE = new BasicAlarmProperty<>("CustomPushoverMessage",
            String.class, "",
            "PushoverNotification.Properties.ExtendedConfig.CustomMessage",
            "PushoverNotification.Properties.ExtendedConfig.Category",
            "PushoverNotification.Properties.ExtendedConfig.CustomMessage.Desc", true, false);

    static {
        MESSAGE.setExpressionSource(true);
        MESSAGE.setDefaultValue(i18n("PushoverNotification." + "Properties.Message.DefaultValue"));

        THROTTLED_MESSAGE.setExpressionSource(true);
        THROTTLED_MESSAGE.setDefaultValue(i18n("PushoverNotification." + "Properties.ThrottledMessage.DefaultValue"));

        TIME_BETWEEN_NOTIFICATIONS.setExpressionSource(true);
        TIME_BETWEEN_NOTIFICATIONS.setDefaultValue(i18n("PushoverNotification."
                + "Properties.TimeBetweenNotifications.DefaultValue"));

        TITLE.setExpressionSource(true);

        PRIORITY.setDefaultValue(0);
        List<ConfigurationProperty.Option<Integer>> priorities = new ArrayList<>();
        priorities.add(new ConfigurationProperty.Option<>(-2, new LocalizedString("PushoverNotification."
                + "Properties.Priority.Lowest")));
        priorities.add(new ConfigurationProperty.Option<>(-1, new LocalizedString("PushoverNotification."
                + "Properties.Priority.Low")));
        priorities.add(new ConfigurationProperty.Option<>(0, new LocalizedString("PushoverNotification."
                + "Properties.Priority.Normal")));
        priorities.add(new ConfigurationProperty.Option<>(1, new LocalizedString("PushoverNotification."
                + "Properties.Priority.High")));
        priorities.add(new ConfigurationProperty.Option<>(2, new LocalizedString("PushoverNotification."
                + "Properties.Priority.Emergency")));
        PRIORITY.setOptions(priorities);

        RETRY.setExpressionSource(true);
        RETRY.setDefaultValue(60);

        EXPIRE.setExpressionSource(true);
        EXPIRE.setDefaultValue(300);

        SOUND.setDefaultValue(i18n("PushoverNotification."
                + "Properties.Sound.DefaultValue"));
        List<ConfigurationProperty.Option<String>> sounds = new ArrayList<>();
        sounds.add(new ConfigurationProperty.Option<>("pushover", new LocalizedString("PushoverNotification."
                + "Properties.Sound.Pushover")));
        sounds.add(new ConfigurationProperty.Option<>("bike", new LocalizedString("PushoverNotification."
                + "Properties.Sound.Bike")));
        sounds.add(new ConfigurationProperty.Option<>("bugle", new LocalizedString("PushoverNotification."
                + "Properties.Sound.Bugle")));
        sounds.add(new ConfigurationProperty.Option<>("cashregister", new LocalizedString("PushoverNotification."
                + "Properties.Sound.CashRegister")));
        sounds.add(new ConfigurationProperty.Option<>("classical", new LocalizedString("PushoverNotification."
                + "Properties.Sound.Classical")));
        sounds.add(new ConfigurationProperty.Option<>("cosmic", new LocalizedString("PushoverNotification."
                + "Properties.Sound.Cosmic")));
        sounds.add(new ConfigurationProperty.Option<>("falling", new LocalizedString("PushoverNotification."
                + "Properties.Sound.Falling")));
        sounds.add(new ConfigurationProperty.Option<>("gamelan", new LocalizedString("PushoverNotification."
                + "Properties.Sound.Gamelan")));
        sounds.add(new ConfigurationProperty.Option<>("incoming", new LocalizedString("PushoverNotification."
                + "Properties.Sound.Incoming")));
        sounds.add(new ConfigurationProperty.Option<>("intermission", new LocalizedString("PushoverNotification."
                + "Properties.Sound.Intermission")));
        sounds.add(new ConfigurationProperty.Option<>("magic", new LocalizedString("PushoverNotification."
                + "Properties.Sound.Magic")));
        sounds.add(new ConfigurationProperty.Option<>("mechanical", new LocalizedString("PushoverNotification."
                + "Properties.Sound.Mechanical")));
        sounds.add(new ConfigurationProperty.Option<>("pianobar", new LocalizedString("PushoverNotification."
                + "Properties.Sound.PianoBar")));
        sounds.add(new ConfigurationProperty.Option<>("siren", new LocalizedString("PushoverNotification."
                + "Properties.Sound.Siren")));
        sounds.add(new ConfigurationProperty.Option<>("spacealarm", new LocalizedString("PushoverNotification."
                + "Properties.Sound.SpaceAlarm")));
        sounds.add(new ConfigurationProperty.Option<>("tugboat", new LocalizedString("PushoverNotification."
                + "Properties.Sound.TugBoat")));
        sounds.add(new ConfigurationProperty.Option<>("alien", new LocalizedString("PushoverNotification."
                + "Properties.Sound.Alien")));
        sounds.add(new ConfigurationProperty.Option<>("climb", new LocalizedString("PushoverNotification."
                + "Properties.Sound.Climb")));
        sounds.add(new ConfigurationProperty.Option<>("persistent", new LocalizedString("PushoverNotification."
                + "Properties.Sound.Persistent")));
        sounds.add(new ConfigurationProperty.Option<>("echo", new LocalizedString("PushoverNotification."
                + "Properties.Sound.Echo")));
        sounds.add(new ConfigurationProperty.Option<>("updown", new LocalizedString("PushoverNotification."
                + "Properties.Sound.UpDown")));
        sounds.add(new ConfigurationProperty.Option<>("vibrate", new LocalizedString("PushoverNotification."
                + "Properties.Sound.Vibrate")));
        sounds.add(new ConfigurationProperty.Option<>("none", new LocalizedString("PushoverNotification."
                + "Properties.Sound.None")));
        SOUND.setOptions(sounds);

        TEST_MODE.setDefaultValue(false);
        List<ConfigurationProperty.Option<Boolean>> options = new ArrayList<>();
        options.add(new ConfigurationProperty.Option<>(true, new LocalizedString("words.yes")));
        options.add(new ConfigurationProperty.Option<>(false, new LocalizedString("words.no")));
        TEST_MODE.setOptions(options);
    }

}
