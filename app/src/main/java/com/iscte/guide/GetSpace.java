package com.iscte.guide;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iscte.guide.models.AppInfo;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;

public class GetSpace extends AppCompatActivity {

    public static final String PREFS_NAME = "MyPrefsFile";

    private String appIdNotFoundMsg = "This ID does not exist, please review";
    private String appIdNotFoundTitle = "Warning - ID not found!";

    private DatabaseReference allowedApps;
    private FirebaseDatabase database;

    private AppInfo appInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_space);

    }

    public void confirmAppButton(View v){

        EditText editText = findViewById(R.id.appID);

        getDBInfo(editText.getText().toString());

    }

    private void getDBInfo(final String spaceID){

        //GET appID, apiKey, dbURL e StorageBucket

        database = FirebaseDatabase.getInstance();

        allowedApps = database.getReference("allowedApps").child(spaceID);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI

                if(dataSnapshot.getValue() != null) {
                    appInfo = dataSnapshot.getValue(AppInfo.class);

                    getAppInstance(spaceID);
                }else{
                    appInfo = null;
                    appIdNotFound();
                }
                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("GetBDValueError", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        allowedApps.addListenerForSingleValueEvent(postListener);


    }

    private void getAppInstance(String spaceID){

        boolean existApp = false;

        for(FirebaseApp appAux: FirebaseApp.getApps(this)){
            if(appAux.getName().equals(spaceID)){
                existApp = true;
                break;
            }
        }

        if(!existApp){
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setApplicationId(appInfo.getApplicationId()) // Required for Analytics.
                    .setApiKey(appInfo.getApiKey()) // Required for Auth.
                    .setDatabaseUrl(appInfo.getDatabaseURL()) // Required for RTDB.
                    .setStorageBucket(appInfo.getStorageBucket())
                    .build();

            FirebaseApp.initializeApp(this,options,spaceID);
        }

        startGuide(spaceID);

    }

    private void startGuide(String spaceID){
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME,0);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("current_space",spaceID);
        editor.commit();

        Intent intent = new Intent(this, Main2Activity.class);
        startActivity(intent);
        finish();
    }

    private void appIdNotFound(){
        AlertDialog.Builder dialBuilder1 = new AlertDialog.Builder(this);

        dialBuilder1.setMessage(appIdNotFoundMsg)
                .setTitle(appIdNotFoundTitle);
        dialBuilder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                dialog.dismiss();
            }
        });

        AlertDialog dialog = dialBuilder1.create();
        dialog.show();
    }
}
