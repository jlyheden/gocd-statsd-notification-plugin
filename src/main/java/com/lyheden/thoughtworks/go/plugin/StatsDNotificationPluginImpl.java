package com.lyheden.thoughtworks.go.plugin;

import com.google.gson.GsonBuilder;
import com.lyheden.thoughtworks.go.plugin.domain.StageStatus;
import com.thoughtworks.go.plugin.api.GoApplicationAccessor;
import com.thoughtworks.go.plugin.api.GoPlugin;
import com.thoughtworks.go.plugin.api.GoPluginIdentifier;
import com.thoughtworks.go.plugin.api.annotation.Extension;
import com.thoughtworks.go.plugin.api.exceptions.UnhandledRequestTypeException;
import com.thoughtworks.go.plugin.api.logging.Logger;
import com.thoughtworks.go.plugin.api.request.GoPluginApiRequest;
import com.thoughtworks.go.plugin.api.response.GoPluginApiResponse;
import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
import java.util.*;

import static java.util.Arrays.asList;

/**
 * Created by johan on 27/06/15.
 */
@Extension
public class StatsDNotificationPluginImpl implements GoPlugin {

    private static Logger LOGGER = Logger.getLoggerFor(StatsDNotificationPluginImpl.class);
    public static final String PLUGIN_ID = "statsd.notifier";
    public static final String EXTENSION_TYPE = "notification";
    public static final String EXTENSION_NAME = "notification";
    private static final List<String> goSupportedVersions = Collections.singletonList("1.0");
    public static final String REQUEST_NOTIFICATIONS_INTERESTED_IN = "notifications-interested-in";
    public static final String REQUEST_STAGE_STATUS = "stage-status";

    public static final int SUCCESS_RESPONSE_CODE = 200;
    public static final int INTERNAL_ERROR_RESPONSE_CODE = 500;

    private StatsDClient statsDClient;

    @Override
    public void initializeGoApplicationAccessor(GoApplicationAccessor goApplicationAccessor) {
        //this.goApplicationAccessor = goApplicationAccessor;
        LOGGER.info("Setting up statsd client");
        statsDClient = new NonBlockingStatsDClient("pipeline.notification", "localhost", 8125);
    }

    @Override
    public GoPluginApiResponse handle(GoPluginApiRequest goPluginApiRequest) throws UnhandledRequestTypeException {
        LOGGER.debug("Received go plugin api request " + goPluginApiRequest.requestName());
        if (goPluginApiRequest.requestName().equals(REQUEST_NOTIFICATIONS_INTERESTED_IN)) {
            return handleNotificationsInterestedIn();
        } else if (goPluginApiRequest.requestName().equals(REQUEST_STAGE_STATUS)) {
            return handleStageNotification(goPluginApiRequest);
        }
        return null;
    }

    @Override
    public GoPluginIdentifier pluginIdentifier() {
        return new GoPluginIdentifier(EXTENSION_NAME, goSupportedVersions);
    }

    private GoPluginApiResponse handleNotificationsInterestedIn() {
        Map<String, List<String>> response = new HashMap<>();
        response.put("notifications", Collections.singletonList(REQUEST_STAGE_STATUS));
        LOGGER.debug("requesting details of stage-status notifications");
        return renderJSON(SUCCESS_RESPONSE_CODE, response);
    }

    private GoPluginApiResponse handleStageNotification(GoPluginApiRequest goPluginApiRequest) {
        LOGGER.info("Handling stage notifications");
        int responseCode = SUCCESS_RESPONSE_CODE;
        Map<String, Object> response = new HashMap<>();
        List<String> messages = new ArrayList<>();
        try {
            String requestBody = goPluginApiRequest.requestBody();
            LOGGER.info("RequestBody from go: " + requestBody);
            StageStatus stageStatus = new GsonBuilder().create().fromJson(requestBody, StageStatus.class);

            LOGGER.info("StageStatus object: " + stageStatus);
            statsDClient.increment(stageStatus.getPipeline().getStageNameExecution());
            statsDClient.increment(stageStatus.getPipeline().getStageStateItemName());
            statsDClient.increment(stageStatus.getPipeline().getStageResultItemName());

            for (Map<String, Long> map : stageStatus.getPipeline().getJobsElapsedTime()) {
                for (Map.Entry<String, Long> entry : map.entrySet()) {
                    statsDClient.recordExecutionTime(entry.getKey(), entry.getValue());
                }
            }

            response.put("status", "success");
            messages.add("Pushed values to statsd");
        } catch (Exception e) {
            LOGGER.error("Exception while serializing request from Go", e);
            responseCode = INTERNAL_ERROR_RESPONSE_CODE;
            response.put("status", "failure");
            messages.add(e.getMessage());
        }

        response.put("messages", messages);
        return renderJSON(responseCode, response);
    }

    private GoPluginApiResponse renderJSON(final int responseCode, Object response) {
        final String json = response == null ? null : new GsonBuilder().create().toJson(response);
        return new GoPluginApiResponse() {
            @Override
            public int responseCode() {
                return responseCode;
            }

            @Override
            public Map<String, String> responseHeaders() {
                return null;
            }

            @Override
            public String responseBody() {
                return json;
            }
        };
    }

}