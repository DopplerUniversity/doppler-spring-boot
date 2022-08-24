package com.doppler.app.config;

public class Doppler {
    String project;
    String environment;
    String config;

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    @Override
    public String toString() {
        return "Doppler {" +
                " project: '" + project + '\'' +
                ", environment: '" + environment + '\'' +
                ", config: '" + config + '\'' +
                " }";
    }
}