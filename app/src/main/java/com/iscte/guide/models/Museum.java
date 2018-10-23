package com.iscte.guide.models;

/**
 * Created by b.coitos on 10/18/2018.
 */

public class Museum {

    private String id;
    private String name;
    private String address;
    private String image;
    private String schedule;
    private String price;
    private float rating;
    private String location;
    private AppInfo appInfo;

    public Museum() {
    }

    public Museum(String id, String name, String address, String image, String schedule, String price, float rating, String location, AppInfo appInfo) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.image = image;
        this.schedule = schedule;
        this.price = price;
        this.rating = rating;
        this.location = location;
        this.appInfo = appInfo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public AppInfo getAppInfo() {
        return appInfo;
    }

    public void setAppInfo(AppInfo appInfo) {
        this.appInfo = appInfo;
    }
}
