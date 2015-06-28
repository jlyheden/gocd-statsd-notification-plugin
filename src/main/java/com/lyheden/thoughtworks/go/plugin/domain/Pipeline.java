package com.lyheden.thoughtworks.go.plugin.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by johan on 27/06/15.
 */
public class Pipeline {

    private String name;
    private String counter;
    private String group;
    private Stage stage;

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

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private String getStageName() {
        return name + "." + stage.getName();
    }

    public String getStageNameExecution() {
        return getStageName() + ".execution";
    }

    public String getStageStateItemName() {
        return getStageName() + ".state." + stage.getState();
    }

    public String getStageResultItemName() {
        return getStageName() + ".result." + stage.getResult();
    }

    public List<Map<String, Long>> getJobsElapsedTime() {
        List<Map<String, Long>> list = new ArrayList<>();
        for (Job job : stage.getJobs()) {
            Map map = new HashMap<>();
            map.put(getStageName() + ".job." + job.getName() + ".time", job.getRunningTime());
            list.add(map);
        }
        return list;
    }

}
