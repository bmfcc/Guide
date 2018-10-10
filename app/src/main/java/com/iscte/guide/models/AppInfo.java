package com.iscte.guide.models;

/**
 * Created by b.coitos on 10/10/2018.
 */

public class AppInfo {

    private String applicationId;
    private String apiKey;
    private String databaseURL;
    private String storageBucket;

    public AppInfo() {
    }

    public AppInfo(String applicationId, String apiKey, String databaseURL, String storageBucket) {
        this.applicationId = applicationId;
        this.apiKey = apiKey;
        this.databaseURL = databaseURL;
        this.storageBucket = storageBucket;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getDatabaseURL() {
        return databaseURL;
    }

    public void setDatabaseURL(String databaseURL) {
        this.databaseURL = databaseURL;
    }

    public String getStorageBucket() {
        return storageBucket;
    }

    public void setStorageBucket(String storageBucket) {
        this.storageBucket = storageBucket;
    }
}
