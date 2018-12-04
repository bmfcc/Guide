package com.iscte.guide.models;

import java.util.List;

/**
 * Created by b.coitos on 5/23/2018.
 */

public class Zone {
    private String name;
    private String id;
    private String startDate;
    private String description;
    private String endDate;
    private List<String> itemsList;

    public Zone(){}

    public Zone(String name, String id, String startDate, String endDate, String description, List<String> itemsList){
        this.name=name;
        this.id=id;
        this.startDate=startDate;
        this.endDate = endDate;
        this.description=description;
        this.itemsList=itemsList;
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

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public List<String> getItemsList() {
        return itemsList;
    }

    public void setItemsList(List<String> itemsList) {
        this.itemsList = itemsList;
    }
}
