package com.lyheden.thoughtworks.go.plugin.domain;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by johan on 27/06/15.
 */
public class StageStatus {

    private Pipeline pipeline;

    private final static DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public Pipeline getPipeline() {
        return pipeline;
    }

    public void setPipeline(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    private String getStageName() {
        return pipeline.getName() + "." + pipeline.getStage().getName();
    }

    public String getStageNameExecution() {
        return getStageName() + ".execution";
    }

    public String getStageStateItemName() {
        return getStageName() + ".state." + pipeline.getStage().getState();
    }

    public String getStageResultItemName() {
        return getStageName() + ".result." + pipeline.getStage().getResult();
    }

    public List<Map<String, Long>> getJobsElapsedTime() {
        List<Map<String, Long>> list = new ArrayList<>();
        for (Job job : pipeline.getStage().getJobs()) {
            if (!job.getCompleteTime().equals("")) {
                Map<String, Long> map = new HashMap<>();
                map.put(getStageName() + ".job." + job.getName() + ".time", job.getRunningTime());
                list.add(map);
            }
        }
        return list;
    }

    public Map<String, Long> getStageExecutionTime() {
        DateTime createTime = formatter.parseDateTime(pipeline.getStage().getCreateTime());
        DateTime lastTransitionTime = formatter.parseDateTime(pipeline.getStage().getLastTransitionTime());
        Map<String, Long> map = new HashMap<>();
        map.put(getStageName() + ".time", lastTransitionTime.getMillis() - createTime.getMillis());
        return map;
    }

}
