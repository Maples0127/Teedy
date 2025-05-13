// UserChatDto.java
package com.sismics.docs.core.dao.dto;

import com.google.common.base.MoreObjects;

/**
 * User chat DTO.
 */
public class UserChatDto {
    private String id;
    private String senderName;
    private String receiverName;
    private String message;
    private Long sendTimestamp;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Long getSendTimestamp() { return sendTimestamp; }
    public void setSendTimestamp(Long sendTimestamp) { this.sendTimestamp = sendTimestamp; }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("sender", senderName)
                .add("receiver", receiverName)
                .add("message", message)
                .toString();
    }
}