package com.lyheden.thoughtworks.go.plugin.domain;

import com.google.gson.annotations.SerializedName;
import org.joda.time.DateTime;

/**
 * Created by johan on 27/06/15.
 */
public class Job {

    private String name;
    private String state;
    private String result;

    @SerializedName("schedule-time") private DateTime scheduleTime;
    @SerializedName("complete-time") private DateTime completeTime;
    @SerializedName("agent-uuid") private String agentUuid;

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

    public DateTime getScheduleTime() {
        return scheduleTime;
    }

    public void setScheduleTime(DateTime scheduleTime) {
        this.scheduleTime = scheduleTime;
    }

    public DateTime getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(DateTime completeTime) {
        this.completeTime = completeTime;
    }

    public String getAgentUuid() {
        return agentUuid;
    }

    public void setAgentUuid(String agentUuid) {
        this.agentUuid = agentUuid;
    }

    public long getRunningTime() {
        return completeTime.getMillis() - scheduleTime.getMillis();
    }

}
