package com.sismics.docs.core.dao.criteria;


/**
 * User registration criteria
 */
public class UserRegistrationCriteria {
    /**
     * Search query.
     */
    private String search;


    /**
     * User ID.
     */
    private String userId;

    /**
     * Username.
     */
    private String userName;


    /**
     * Status.
     */
    private String status;

    public String getSearch() {
        return search;
    }

    public UserRegistrationCriteria setSearch(String search) {
        this.search = search;
        return this;
    }


    public String getUserId() {
        return userId;
    }

    public UserRegistrationCriteria setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getUserName() {
        return userName;
    }

    public UserRegistrationCriteria setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public UserRegistrationCriteria setStatus(String status) {
        this.status = status;
        return this;
    }
}