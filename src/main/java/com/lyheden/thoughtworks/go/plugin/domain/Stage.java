package com.lyheden.thoughtworks.go.plugin.domain;

import com.google.gson.annotations.SerializedName;
import org.joda.time.DateTime;

import java.util.Date;

/**
 * Created by johan on 27/06/15.
 */
public class Stage {

    private String name;
    private String counter;
    private String state;
    private String result;
    private Job[] jobs;

    @SerializedName("approval-type") private String approvalType;
    @SerializedName("approved-by") private String approvedBy;
    @SerializedName("create-time") private String createTime;
    @SerializedName("last-transition-time") private String lastTransitionTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCounter() {
        return counter;
    }

    public void setCounter(String counter) {
        this.counter = counter;
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

    public Job[] getJobs() {
        return jobs;
    }

    public void setJobs(Job[] jobs) {
        this.jobs = jobs;
    }

    public String getApprovalType() {
        return approvalType;
    }

    public void setApprovalType(String approvalType) {
        this.approvalType = approvalType;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getLastTransitionTime() {
        return lastTransitionTime;
    }

    public void setLastTransitionTime(String lastTransitionTime) {
        this.lastTransitionTime = lastTransitionTime;
    }

}
