package com.iscte.guide.models;

/**
 * Created by b.coitos on 11/8/2018.
 */

public class Item {
    private String name;
    private String id;
    private String audioFile;
    private String description;
    private String imageFile;

    public Item(){}

    public Item(String name, String id, String audioFile, String imageFile, String description){
        this.name=name;
        this.id=id;
        this.audioFile=audioFile;
        this.imageFile = imageFile;
        this.description=description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAudioFile() {
        return audioFile;
    }

    public void setAudioFile(String audioFile) {
        this.audioFile = audioFile;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageFile() {
        return imageFile;
    }

    public void setImageFile(String imageFile) {
        this.imageFile = imageFile;
    }

}
