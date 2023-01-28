package com.kyvislabs.pushover.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

public class PushoverClient {
    private final CloseableHttpClient httpClient = HttpClientBuilder.create().build();
    private Logger logger = Logger.getLogger("pushover.client");

    public boolean sendMessage(String device, String token, String userKey, String message, String sound, Integer priority, String retry, String expire, String title){

        boolean success = true;
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

        if (!StringUtils.isBlank(title)) {
            postParameters.add(new BasicNameValuePair("title", title));
        }

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

        return success;
    }
}
