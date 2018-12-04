package com.iscte.guide;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iscte.guide.models.Exhibition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExhibitionList extends AppCompatActivity {

    private ExpandableListAdapter listAdapter;
    private ExpandableListView expListView;
    private List<String> listExhibitionNames;
    private HashMap<String, Exhibition> exhibitionInfo;

    private String mySpace;
    public static final String PREFS_NAME = "MyPrefsFile";
    private String language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exhibition_list);

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME,0);
        mySpace = preferences.getString("current_space", "Default");
        language = preferences.getString("selected_language","Default");

        expListView = (ExpandableListView) findViewById(R.id.exhibition_ELV);

        prepareListData();

    }

    private void prepareListData() {

        FirebaseApp app = FirebaseApp.getInstance(mySpace);
        FirebaseDatabase database = FirebaseDatabase.getInstance(app);

        DatabaseReference dbRef = database.getReference("Exhibition").child(language);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI

                listExhibitionNames = new ArrayList<>();
                exhibitionInfo = new HashMap<>();

                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    Exhibition ex = snapshot.getValue(Exhibition.class);
                    listExhibitionNames.add(ex.getName());
                    exhibitionInfo.put(ex.getName(),ex);
                }

                listAdapter = new ExhibitionListAdapter(getApplicationContext(), listExhibitionNames, exhibitionInfo);

                expListView.setAdapter(listAdapter);

                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("GetBDValueError", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        dbRef.addListenerForSingleValueEvent(postListener);

    }

}
