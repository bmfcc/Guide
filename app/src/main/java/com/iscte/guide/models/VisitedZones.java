package com.iscte.guide.models;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by b.coitos on 9/30/2018.
 */

public class VisitedZones {

    private String visitedZones;
    private ArrayList<String> visitedZonesArr;
    private JSONObject visitedZonesJson;

    public VisitedZones(String visitedZones, String mySpace) {

        this.visitedZones=visitedZones;
        toJson();
        jsonToArray(mySpace);
    }

    @Override
    public String toString(){
        //visitedZones = TextUtils.join(";",visitedZonesArr);
        return visitedZonesJson.toString();
    }

    public void addZone(String zoneID,String mySpace){

        if(!visitedZonesArr.contains(zoneID)) {
            visitedZonesArr.add(zoneID);
            updateJson(mySpace);
        }

    }

    private void jsonToArray(String mySpace) {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<String>>(){}.getType();

        try {
            visitedZonesArr = gson.fromJson(visitedZonesJson.getString(mySpace), type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateJson(String mySpace) {
        try {
            visitedZonesJson.put(mySpace,visitedZonesArr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void toJson(){
        try {
            this.visitedZonesJson = new JSONObject(visitedZones);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public ArrayList<String> getVisitedZonesArr(){
        return visitedZonesArr;
    }
}
