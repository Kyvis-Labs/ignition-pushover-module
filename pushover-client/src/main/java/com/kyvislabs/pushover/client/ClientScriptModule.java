package com.kyvislabs.pushover.client;

import com.inductiveautomation.ignition.client.gateway_interface.ModuleRPCFactory;
import com.inductiveautomation.ignition.common.script.hints.NoHint;
import com.kyvislabs.pushover.common.scripting.AbstractScriptModule;
import com.kyvislabs.pushover.common.scripting.PushoverClientScripts;

public class ClientScriptModule extends AbstractScriptModule {
    private final PushoverClientScripts rpc;
    public ClientScriptModule() {
        rpc = ModuleRPCFactory.create(
            "com.kyvislabs.pushover",
            PushoverClientScripts.class
        );
    }

    @NoHint
    @Override
    protected boolean sendMessageImpl(String device, String token, String userKey, String message, String sound,
            Integer priority, String retry, String expire, String title) {
        return rpc.sendMessage(device, token, userKey, message, sound,
        priority, retry, expire, title);
    }
}
