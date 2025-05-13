// UserChatCriteria.java
package com.sismics.docs.core.dao.criteria;

/**
 * User chat criteria.
 */
public class UserChatCriteria {
    private String senderName;
    private String receiverName;
    private UserChatCriteria combinedCriteria;


    public String getSenderName() { return senderName; }
    public UserChatCriteria setSenderName(String senderName) {
        this.senderName = senderName;
        return this;
    }

    public String getReceiverName() { return receiverName; }
    public UserChatCriteria setReceiverName(String receiverName) {
        this.receiverName = receiverName;
        return this;
    }

    public UserChatCriteria getCombinedCriteria() {
        return combinedCriteria;
    }

    public void setCombinedCriteria(UserChatCriteria combinedCriteria) {
        this.combinedCriteria = combinedCriteria;
    }
}