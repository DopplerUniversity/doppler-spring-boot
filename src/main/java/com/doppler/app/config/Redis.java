package com.doppler.app.config;

public class Redis {
    String host;
    Integer port;
    String user;
    String password;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Redis {" +
                " host: '" + host + '\'' +
                ", port: " + port +
                ", user: '" + user + '\'' +
                ", password: '" + password + '\'' +
                " }";
    }
}