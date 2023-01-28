package com.kyvislabs.pushover.common.scripting;

import org.apache.log4j.Logger;
import org.python.core.PyObject;
import org.python.netty.util.internal.StringUtil;

import com.inductiveautomation.ignition.common.BundleUtil;
import com.inductiveautomation.ignition.common.script.builtin.KeywordArgs;
import com.inductiveautomation.ignition.common.script.builtin.PyArgumentMap;
import com.inductiveautomation.ignition.common.script.hints.NoHint;
import com.inductiveautomation.ignition.common.script.hints.ScriptFunction;

public abstract class AbstractScriptModule implements PushoverClientScripts 
{
    static {
        BundleUtil.get().addBundle(
            AbstractScriptModule.class.getSimpleName(),
            AbstractScriptModule.class.getClassLoader(),
            AbstractScriptModule.class.getName().replace('.', '/')
        );
    }
    public Logger logger = Logger.getLogger("pushover.scripting.client");

    @ScriptFunction(docBundlePrefix = "AbstractScriptModule")
    @KeywordArgs(
        names={"device", "token", "userKey", "message", "sound", "priority", "retry", "expire", "title"}, 
        types={String.class, String.class, String.class, String.class, String.class, Integer.class, String.class, String.class, String.class}
    )
    public boolean sendMessage(PyObject[] pyArgs, String[] keywords) throws Exception {
            PyArgumentMap args = PyArgumentMap.interpretPyArgs(pyArgs, keywords, AbstractScriptModule.class, "sendMessage");    

            String device = args.getStringArg("device");
            if (StringUtil.isNullOrEmpty(device)){
                logger.error("Invalid device id");
                return false;
            }

            String token = args.getStringArg("token");
            if (StringUtil.isNullOrEmpty(token)){
                logger.error("Invalid token");
                return false;

            }

            String userKey = args.getStringArg("userKey");
            if (StringUtil.isNullOrEmpty(userKey)){
                logger.error("User Key is blank or null");
                return false;
            }

            String message = args.getStringArg("message");
            if (StringUtil.isNullOrEmpty(message)){
                logger.error("Message is blank or null");
                return false;
            }

            String sound = args.getStringArg("sound");
            Integer priority = args.getIntArg("priority");
            String retry = args.getStringArg("retry");
            String expire = args.getStringArg("expire");
            String title = args.getStringArg("title");
            return sendMessageImpl(device, token, userKey, message, sound, priority, retry, expire, title);
    }

    @NoHint
    @Override
    public boolean sendMessage(String device, String token, String userKey, String message, String sound, Integer priority, String retry, String expire, String title) {
        return sendMessageImpl(device, token, userKey, message, sound, priority, retry, expire, title);
    }

    @NoHint
    protected abstract boolean sendMessageImpl(String device, String token, String userKey, String message, String sound, Integer priority, String retry, String expire, String title);
}