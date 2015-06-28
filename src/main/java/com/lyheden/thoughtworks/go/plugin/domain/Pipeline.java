package com.lyheden.thoughtworks.go.plugin.domain;

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

}
