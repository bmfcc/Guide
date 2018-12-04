package com.iscte.guide.models;

/**
 * Created by b.coitos on 11/14/2018.
 */

public class Exhibition {

    private String id;
    private String name;
    private String description;
    private String calendar;
    private String imageFile;

    public Exhibition() {
    }

    public Exhibition(String id, String name, String description, String calendar, String imageFile) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.calendar = calendar;
        this.imageFile=imageFile;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCalendar() {
        return calendar;
    }

    public void setCalendar(String calendar) {
        this.calendar = calendar;
    }

    public String getImageFile() {
        return imageFile;
    }

    public void setImageFile(String imageFile) {
        this.imageFile = imageFile;
    }
}
