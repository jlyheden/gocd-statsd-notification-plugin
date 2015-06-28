package com.lyheden.thoughtworks.go.plugin.domain;

import com.google.gson.annotations.SerializedName;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by johan on 27/06/15.
 */
public class Job {

    private String name;
    private String state;
    private String result;

    @SerializedName("schedule-time") private String scheduleTime;
    @SerializedName("complete-time") private String completeTime;
    @SerializedName("agent-uuid") private String agentUuid;

    private final static DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(String scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    public String getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(String completeTime) {
        this.completeTime = completeTime;
    }

    public String getAgentUuid() {
        return agentUuid;
    }

    public void setAgentUuid(String agentUuid) {
        this.agentUuid = agentUuid;
    }

    public long getRunningTime() {
        DateTime completeTimeDateTime = formatter.parseDateTime(completeTime);
        DateTime scheduleTimeDateTime = formatter.parseDateTime(scheduleTime);
        return completeTimeDateTime.getMillis() - scheduleTimeDateTime.getMillis();
    }

}
