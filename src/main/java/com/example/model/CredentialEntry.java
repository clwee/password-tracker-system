package com.example.password_tracker_system.model;

public class CredentialEntry {

    private String serviceName;
    private String username;
    private String password;

    public CredentialEntry(String serviceName, String username, String password) {
        this.serviceName = serviceName;
        this.username = username;
        this.password = password;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}