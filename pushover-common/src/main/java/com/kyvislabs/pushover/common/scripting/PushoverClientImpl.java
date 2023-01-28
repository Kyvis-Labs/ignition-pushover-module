package com.kyvislabs.pushover.common.scripting;


import com.kyvislabs.pushover.common.PushoverClient;

public class PushoverClientImpl extends AbstractScriptModule {

    private PushoverClient client = new PushoverClient();
    @Override
    protected boolean sendMessageImpl(String device, String token, String userKey, String message, String sound, Integer priority, String retry, String expire, String title) {
        logger.debug("Sending Message");
        return client.sendMessage(device, token, userKey, message, sound, priority, retry, expire, title);
    }

}
