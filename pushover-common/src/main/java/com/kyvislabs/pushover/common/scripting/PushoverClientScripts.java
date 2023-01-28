package com.kyvislabs.pushover.common.scripting;

public interface PushoverClientScripts {
    public boolean sendMessage(String device, String token, String userKey, String message, String sound, Integer priority, String retry, String expire, String title);
}