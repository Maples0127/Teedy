package com.sismics.docs.core.dao.dto;

import com.google.common.base.MoreObjects;

public class UserRegistrationDto {
    private String id;
    private String username;
    private String email;
    private Long createTimestamp;
    private String status;
    private Long disableTimestamp;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getCreateTimestamp() {
        return createTimestamp;
    }

    public void setCreateTimestamp(Long createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("username", username)
                .add("email", email)
                .toString();
    }

    public Long getDisableTimestamp() {
        return disableTimestamp;
    }

    public void setDisableTimestamp(Long disableTimestamp) {
        this.disableTimestamp = disableTimestamp;
    }
}