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
import com.timgroup.statsd.StatsDClient;
import java.util.*;

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

    private static StatsDClient statsDClient = StatsDClientFactory.getNonBlockingStatsDClient();

    @Override
    public void initializeGoApplicationAccessor(GoApplicationAccessor goApplicationAccessor) {
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
        LOGGER.debug("Handling stage notifications");
        int responseCode = SUCCESS_RESPONSE_CODE;
        Map<String, Object> response = new HashMap<>();
        List<String> messages = new ArrayList<>();
        try {
            String requestBody = goPluginApiRequest.requestBody();
            LOGGER.debug("RequestBody from go: " + requestBody);
            StageStatus stageStatus = new GsonBuilder().create().fromJson(requestBody, StageStatus.class);

            statsDClient.increment(stageStatus.getStageNameExecution());
            messages.add("Incremented metric " + stageStatus.getStageNameExecution());

            statsDClient.increment(stageStatus.getStageStateItemName());
            messages.add("Incremented metric " + stageStatus.getStageStateItemName());

            statsDClient.increment(stageStatus.getStageResultItemName());
            messages.add("Incremented metric " + stageStatus.getStageResultItemName());

            if (!stageStatus.getPipeline().getStage().getLastTransitionTime().isEmpty()) {
                for (Map.Entry<String, Long> entry : stageStatus.getStageExecutionTime().entrySet()) {
                    statsDClient.recordExecutionTime(entry.getKey(), entry.getValue());
                    statsDClient.gauge(entry.getKey(), entry.getValue());
                    messages.add("Recorded stage execution time key " + entry.getKey() + " value " + String.valueOf(entry.getValue()));
                }
            }

            for (Map<String, Long> map : stageStatus.getJobsElapsedTime()) {
                for (Map.Entry<String, Long> entry : map.entrySet()) {
                    statsDClient.recordExecutionTime(entry.getKey(), entry.getValue());
                    statsDClient.gauge(entry.getKey(), entry.getValue());
                    messages.add("Recorded execution time key " + entry.getKey() + " value " + String.valueOf(entry.getValue()));
                }
            }

            response.put("status", "success");
            LOGGER.info("Pushed metrics to StatsD for pipeline " + stageStatus.getPipeline().getName() + " stage " + stageStatus.getPipeline().getStage().getName());
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