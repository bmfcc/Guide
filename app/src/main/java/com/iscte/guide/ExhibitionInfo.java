package com.iscte.guide;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.iscte.guide.models.AppInfo;
import com.iscte.guide.models.Exhibition;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;

public class ExhibitionInfo extends AppCompatActivity {

    private String mySpace;
    private String language;
    public static final String PREFS_NAME = "MyPrefsFile";

    private Exhibition exhibition;
    private FirebaseApp app;
    private FirebaseDatabase database;

    private AppInfo appInfo;

    private String exhibitionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exhibition_info);

        Intent myIntent = getIntent(); // gets the previously created intent
        exhibitionId = myIntent.getStringExtra("exhibition");

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME,0);
        language = preferences.getString("selected_language", "EN");

        mySpace = preferences.getString("current_space", "Default");


        getDBInstance();
    }

    private void getDBInstance(){

        database = FirebaseDatabase.getInstance();

        DatabaseReference allowedApps = database.getReference("museums").child(mySpace).child("appInfo");

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI

                appInfo = dataSnapshot.getValue(AppInfo.class);

                getAppInstance();
                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("GetBDValueError", "loadPost:onCancelled", databaseError.toException());
                appInfo = null;
                // ...
            }
        };
        allowedApps.addListenerForSingleValueEvent(postListener);

    }

    private void getAppInstance() {

        boolean existApp = false;

        for (FirebaseApp appAux : FirebaseApp.getApps(this)) {
            if (appAux.getName().equals(mySpace)) {
                existApp = true;
                break;
            }
        }

        if (!existApp) {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setApplicationId(appInfo.getApplicationId()) // Required for Analytics.
                    .setApiKey(appInfo.getApiKey()) // Required for Auth.
                    .setDatabaseUrl(appInfo.getDatabaseURL()) // Required for RTDB.
                    .setStorageBucket(appInfo.getStorageBucket())
                    .build();

            FirebaseApp.initializeApp(this, options, mySpace);
        }

        app = FirebaseApp.getInstance(mySpace);
        database = FirebaseDatabase.getInstance(app);

        getDBInfo(exhibitionId);
    }

    private void getDBInfo(String exhibitionId){

        Log.e("LINGUA", "lingua: " + language);
        DatabaseReference exhibitionRef = database.getReference("Exhibition").child(language).child(exhibitionId);


        ValueEventListener postListenerExhibition = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI

                exhibition = dataSnapshot.getValue(Exhibition.class);

                prepareActivity();

                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("GetBDValueError", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        exhibitionRef.addListenerForSingleValueEvent(postListenerExhibition);
    }

    private void prepareActivity(){

        buildTV();
        buildIV();

    }

    private void buildTV(){
        TextView exhibitionName = (TextView) findViewById(R.id.exhibitionNameTV);
        TextView exhibitionDesc = (TextView) findViewById(R.id.exhibitionDescTV);
        exhibitionDesc.setMovementMethod(new ScrollingMovementMethod());

        exhibitionName.setText(exhibition.getName());
        exhibitionDesc.setText(exhibition.getDescription());
    }

    private void buildIV(){
        FirebaseStorage storage = FirebaseStorage.getInstance(app);
        StorageReference storageRef = storage.getReference();
        StorageReference exhibitionRef = storageRef.child("Images").child("Exhibitions");

        ImageView exhibitionImage = (ImageView) findViewById(R.id.exhibitionImage);

        StorageReference imageReference = exhibitionRef.child(exhibition.getImageFile());

        GlideApp.with(this).load(imageReference).into(exhibitionImage);
    }
}
