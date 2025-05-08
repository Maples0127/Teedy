package com.sismics.docs.core.model.jpa;

import com.google.common.base.MoreObjects;
import jakarta.persistence.*;

import java.util.Date;

/**
 * User registration request entity.
 */
@Entity
@Table(name = "T_USER_REGISTRATION")
public class UserRegistration implements Loggable {
    @Id
    @Column(name = "URQ_ID_C", length = 36)
    private String id;

    @Column(name = "URQ_USERNAME_C", nullable = false, length = 50, unique = true)
    private String username;

    @Column(name = "URQ_PASSWORD_C", nullable = false, length = 100)
    private String password;

    @Column(name = "URQ_EMAIL_C", length = 100)
    private String email;

    @Column(name = "URQ_STORAGEQUOTA_N", nullable = false)
    private Long storageQuota;

    @Enumerated(EnumType.STRING)
    @Column(name = "URQ_STATUS_C", nullable = false, length = 20)
    private String status = "pending";

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

    public UserRegistration setId(String id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public UserRegistration setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public UserRegistration setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public UserRegistration setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public UserRegistration setStatus(String status) {
        this.status = status;
        return this;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public UserRegistration setCreateDate(Date createDate) {
        this.createDate = createDate;
        return this;
    }


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

    public Long getStorageQuota() {
        return storageQuota;
    }

    public void setStorageQuota(Long storageQuota) {
        this.storageQuota = storageQuota;
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