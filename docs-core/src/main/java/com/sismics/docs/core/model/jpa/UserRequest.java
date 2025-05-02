package com.sismics.docs.core.model.jpa;

import com.google.common.base.MoreObjects;
import jakarta.persistence.*;

import java.util.Date;

/**
 * User registration request entity.
 */
@Entity
@Table(name = "T_USER_REQUEST")
public class UserRequest implements Loggable {


    public Date getDeleteDate() {
        return deleteDate;
    }

    public void setDeleteDate(Date deleteDate) {
        this.deleteDate = deleteDate;
    }

    public Date getDisableDate() {
        return disableDate;
    }

    public void setDisableDate(Date disableDate) {
        this.disableDate = disableDate;
    }

    public enum Status {
        PENDING,  // 待审批状态（对应初始空值）
        ACCEPTED, // 审批通过（对应您提到的 1）
        REJECTED  // 审批拒绝（对应您提到的 0）
    }

    @Id
    @Column(name = "URQ_ID_C", length = 36)
    private String id;

    @Column(name = "URQ_USERNAME_C", nullable = false, length = 50, unique = true)
    private String username;

    @Column(name = "URQ_PASSWORD_C", nullable = false, length = 100)
    private String password;

    @Column(name = "URQ_EMAIL_C", length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "URQ_STATUS_C", nullable = false, length = 20)
    private Status status = Status.PENDING;

    @Column(name = "URQ_CREATEDATE_D", nullable = false)
//    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    @Column(name = "URQ_DELETEDATE_D", nullable = false)
    private Date deleteDate;

    @Column(name = "URQ_DISABLEDATE_D", nullable = false)
    private Date disableDate;

    // region Getters/Setters
    public String getId() {
        return id;
    }

    public UserRequest setId(String id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public UserRequest setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public UserRequest setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserRequest setEmail(String email) {
        this.email = email;
        return this;
    }

    public Status getStatus() {
        return status;
    }

    public UserRequest setStatus(Status status) {
        this.status = status;
        return this;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public UserRequest setCreateDate(Date createDate) {
        this.createDate = createDate;
        return this;
    }
    // endregion

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("username", username)
                .add("status", status)
                .toString();
    }

    @Override
    public String toMessage() {
        return username + " is " + status;
    }
}