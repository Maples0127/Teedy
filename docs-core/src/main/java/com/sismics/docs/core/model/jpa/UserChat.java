package com.sismics.docs.core.model.jpa;

import com.google.common.base.MoreObjects;
import jakarta.persistence.*;
import java.util.Date;

/**
 * User chat entity.
 */
@Entity
@Table(name = "T_USER_CHAT")
public class UserChat{
    /**
     * Chat ID.
     */
    @Id
    @Column(name = "UCH_ID_C", length = 36)
    private String id;

    /**
     * Sender user ID.
     */
    @Column(name = "UCH_SENDER_NAME_C", nullable = false, length = 50)
    private String senderName;

    /**
     * Receiver user ID.
     */
    @Column(name = "UCH_RECEIVER_NAME_C", nullable = false, length = 50)
    private String receiverName;

    /**
     * Chat message content.
     */
    @Column(name = "UCH_MESSAGE_C", nullable = false, length = 4000)
    private String message;

    /**
     * Message send date.
     */
    @Column(name = "UCH_SENDDATE_D", nullable = false)
    private Date sendDate;

    // Getters and Setters with chaining
    public String getId() {
        return id;
    }

    public UserChat setId(String id) {
        this.id = id;
        return this;
    }

    public String getSenderName() {
        return senderName;
    }

    public UserChat setSenderName(String senderName) {
        this.senderName = senderName;
        return this;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public UserChat setReceiverName(String receiverName) {
        this.receiverName = receiverName;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public UserChat setMessage(String message) {
        this.message = message;
        return this;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public UserChat setSendDate(Date sendDate) {
        this.sendDate = sendDate;
        return this;
    }


    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("senderName", senderName)
                .add("receiverName", receiverName)
                .add("message", message)
                .toString();
    }
}