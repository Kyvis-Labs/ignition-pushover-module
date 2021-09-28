package com.kyvislabs.pushover.gateway.profile;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.inductiveautomation.ignition.alarming.common.notification.BasicNotificationProfileProperty;
import com.inductiveautomation.ignition.alarming.common.notification.NotificationProfileProperty;
import com.inductiveautomation.ignition.alarming.notification.AlarmNotificationProfile;
import com.inductiveautomation.ignition.alarming.notification.AlarmNotificationProfileRecord;
import com.inductiveautomation.ignition.alarming.notification.NotificationContext;
import com.inductiveautomation.ignition.common.TypeUtilities;
import com.inductiveautomation.ignition.common.WellKnownPathTypes;
import com.inductiveautomation.ignition.common.alarming.AlarmEvent;
import com.inductiveautomation.ignition.common.config.FallbackPropertyResolver;
import com.inductiveautomation.ignition.common.expressions.parsing.Parser;
import com.inductiveautomation.ignition.common.expressions.parsing.StringParser;
import com.inductiveautomation.ignition.common.model.ApplicationScope;
import com.inductiveautomation.ignition.common.model.values.QualifiedValue;
import com.inductiveautomation.ignition.common.sqltags.model.types.DataQuality;
import com.inductiveautomation.ignition.common.user.ContactInfo;
import com.inductiveautomation.ignition.common.user.ContactType;
import com.inductiveautomation.ignition.gateway.audit.AuditProfile;
import com.inductiveautomation.ignition.gateway.audit.AuditRecord;
import com.inductiveautomation.ignition.gateway.audit.AuditRecordBuilder;
import com.inductiveautomation.ignition.gateway.expressions.AlarmEventCollectionExpressionParseContext;
import com.inductiveautomation.ignition.gateway.expressions.FormattedExpressionParseContext;
import com.inductiveautomation.ignition.gateway.localdb.persistence.PersistenceSession;
import com.inductiveautomation.ignition.gateway.model.GatewayContext;
import com.inductiveautomation.ignition.gateway.model.ProfileStatus;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class PushoverNotificationProfile implements AlarmNotificationProfile {

    private final GatewayContext context;
    private String auditProfileName, profileName, token, userKey;
    private final ScheduledExecutorService executor;
    private volatile ProfileStatus profileStatus = ProfileStatus.UNKNOWN;
    private Logger logger;

    public PushoverNotificationProfile(final GatewayContext context,
                                       final AlarmNotificationProfileRecord profileRecord,
                                       final PushoverNotificationProfileSettings settingsRecord) {
        this.context = context;
        this.executor = Executors.newSingleThreadScheduledExecutor();
        this.profileName = profileRecord.getName();
        this.token = settingsRecord.getAPIToken();
        this.userKey = settingsRecord.getUserKey();

        this.logger = Logger.getLogger(String.format("Pushover.%s.Profile", this.profileName));

        try (PersistenceSession session = context.getPersistenceInterface().getSession(settingsRecord.getDataSet())) {
            auditProfileName = settingsRecord.getAuditProfileName();
        } catch (Exception e) {
            logger.error("Error retrieving notification profile details.", e);
        }

    }

    @Override
    public String getName() {
        return profileName;
    }

    @Override
    public Collection<NotificationProfileProperty<?>> getProperties() {
        return Lists.newArrayList(
                PushoverProperties.MESSAGE,
                PushoverProperties.THROTTLED_MESSAGE,
                PushoverProperties.TITLE,
                PushoverProperties.PRIORITY,
                PushoverProperties.RETRY,
                PushoverProperties.EXPIRE,
                PushoverProperties.SOUND,
                PushoverProperties.TEST_MODE
        );
    }

    @Override
    public ProfileStatus getStatus() {
        return profileStatus;
    }

    @Override
    public Collection<ContactType> getSupportedContactTypes() {
        return Lists.newArrayList(PushoverNotificationProfileType.PUSHOVER);
    }

    @Override
    public void onShutdown() {
        executor.shutdown();
    }

    @Override
    public void onStartup() {
        profileStatus = ProfileStatus.RUNNING;
    }

    @Override
    public void sendNotification(final NotificationContext notificationContext) {
        executor.execute(() -> {
            Collection<ContactInfo> contactInfos =
                    Collections2.filter(notificationContext.getUser().getContactInfo(), new IsPushoverContactInfo());

            String message = evaluateMessageExpression(notificationContext, PushoverProperties.MESSAGE);
            String title = evaluateMessageExpression(notificationContext, PushoverProperties.TITLE);
            Integer priority = notificationContext.getOrDefault(PushoverProperties.PRIORITY);
            String sound = notificationContext.getOrDefault(PushoverProperties.SOUND);

            String retry = evaluateMessageExpression(notificationContext, PushoverProperties.RETRY);
            String expire = evaluateMessageExpression(notificationContext, PushoverProperties.EXPIRE);

            boolean testMode = notificationContext.getOrDefault(PushoverProperties.TEST_MODE);
            boolean success = true;
            if (testMode) {
                logger.info(
                        String.format("THIS PROFILE IS RUNNING IN TEST MODE. The following WOULD have been sent:\nMessage: %s, Sound=%s, Priority=%s, Title=%s",
                                message, sound, priority.toString(), title)
                );

                notificationContext.notificationDone();
                return;
            }

            try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
                for (ContactInfo contactInfo : contactInfos) {
                    String device = contactInfo.getValue();

                    List<NameValuePair> postParameters = new ArrayList<>();
                    postParameters.add(new BasicNameValuePair("token", token));
                    postParameters.add(new BasicNameValuePair("user", userKey));
                    postParameters.add(new BasicNameValuePair("message", message));
                    postParameters.add(new BasicNameValuePair("device", device));
                    postParameters.add(new BasicNameValuePair("sound", sound));
                    postParameters.add(new BasicNameValuePair("priority", priority.toString()));

                    if (priority == 2) {
                        // Emergency requires retry and expire
                        postParameters.add(new BasicNameValuePair("retry", retry));
                        postParameters.add(new BasicNameValuePair("expire", expire));
                    }

                    if (title != null) {
                        postParameters.add(new BasicNameValuePair("title", title));
                    }

                    logger.debug(
                            String.format("Attempting to send an alarm notification to %s via %s [message=%s, sound=%s, priority=%s, title=%s]",
                                    notificationContext.getUser(),
                                    device,
                                    message,
                                    sound,
                                    priority.toString(),
                                    title)
                    );

                    HttpPost request = new HttpPost("https://api.pushover.net/1/messages.json");
                    try {
                        request.setEntity(new UrlEncodedFormEntity(postParameters, "UTF-8"));
                        CloseableHttpResponse response = httpClient.execute(request);
                        if (!(response.getStatusLine().getStatusCode() >= 200 && response.getStatusLine().getStatusCode() <= 299)) {
                            logger.error("Error sending notification: status code=" + response.getStatusLine().getStatusCode() + ", response=" + response.getStatusLine().getReasonPhrase());
                        }
                    } catch (IOException e) {
                        logger.error("Unable to send notification", e);
                        success = false;
                    }

                    audit(success, String.format("Pushover message to %s", device), notificationContext);
                }
            } catch (IOException ex) {
                logger.error("Unable to send notification", ex);
            }

            notificationContext.notificationDone();
        });
    }

    private void audit(boolean success, String eventDesc, NotificationContext notificationContext) {
        if (!StringUtils.isBlank(auditProfileName)) {
            try {
                AuditProfile p = context.getAuditManager().getProfile(auditProfileName);
                if (p == null) {
                    return;
                }
                List<AlarmEvent> alarmEvents = notificationContext.getAlarmEvents();
                for (AlarmEvent event : alarmEvents) {
                    AuditRecord r = new AuditRecordBuilder()
                            .setAction(eventDesc)
                            .setActionTarget(
                                    event.getSource().extend(WellKnownPathTypes.Event, event.getId().toString())
                                            .toString())
                            .setActionValue(success ? "SUCCESS" : "FAILURE")
                            .setActor(notificationContext.getUser().getPath().toString())
                            .setActorHost(profileName)
                            .setOriginatingContext(ApplicationScope.GATEWAY)
                            .setOriginatingSystem("Alarming")
                            .setStatusCode(success ? DataQuality.GOOD_DATA.getIntValue() : 0)
                            .setTimestamp(new Date())
                            .build();
                    p.audit(r);
                }
            } catch (Exception e) {
                logger.error("Error auditing event.", e);
            }
        }
    }

    private String evaluateMessageExpression(NotificationContext notificationContext, BasicNotificationProfileProperty property) {
        Parser parser = new StringParser();

        FallbackPropertyResolver resolver =
                new FallbackPropertyResolver(context.getAlarmManager().getPropertyResolver());

        FormattedExpressionParseContext parseContext =
                new FormattedExpressionParseContext(
                        new AlarmEventCollectionExpressionParseContext(resolver, notificationContext.getAlarmEvents()));

        String expressionString = null;

        if (property.equals(PushoverProperties.MESSAGE)) {
            String customMessage = notificationContext.getAlarmEvents().get(0).get(PushoverProperties.CUSTOM_MESSAGE);
            boolean isThrottled = notificationContext.getAlarmEvents().size() > 1;

            if (isThrottled || StringUtils.isBlank(customMessage)) {
                expressionString = isThrottled ?
                        notificationContext.getOrDefault(PushoverProperties.THROTTLED_MESSAGE) :
                        notificationContext.getOrDefault(PushoverProperties.MESSAGE);
            } else {
                expressionString = customMessage;
            }
        } else if (property.equals(PushoverProperties.RETRY) || property.equals(PushoverProperties.EXPIRE)) {
            expressionString = notificationContext.getOrDefault(property).toString();
        } else {
            expressionString = (String) notificationContext.getOrDefault(property);
        }

        if (expressionString == null) {
            return null;
        }

        String evaluated = expressionString;
        try {
            QualifiedValue value = parser.parse(expressionString, parseContext).execute();
            if (value.getQuality().isGood()) {
                evaluated = TypeUtilities.toString(value.getValue());
            }
        } catch (Exception e) {
            logger.error(String.format("Error parsing expression '%s'.", expressionString, e));
        }

        logger.trace(String.format("%s evaluated to '%s'.", property.toString(), evaluated));

        return evaluated;
    }

    /**
     * A {@link Predicate} that returns true if a {@link ContactInfo}'s {@link ContactType} is Console.
     */
    private static class IsPushoverContactInfo implements Predicate<ContactInfo> {
        @Override
        public boolean apply(ContactInfo contactInfo) {
            return PushoverNotificationProfileType.PUSHOVER.getContactType().equals(contactInfo.getContactType());
        }
    }

}
