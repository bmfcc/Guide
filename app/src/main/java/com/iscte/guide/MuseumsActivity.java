package com.iscte.guide;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.iscte.guide.models.Museum;

import java.util.ArrayList;

public class MuseumsActivity extends AppCompatActivity {

    private RecyclerView museumsRV;
    private ArrayList<Museum> museumsList;
    private RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_museums);

        museumsRV = findViewById(R.id.museums_RV);
        museumsRV.setHasFixedSize(true);

        museumsRV.setLayoutManager(new LinearLayoutManager(this));

        getDBInfo();
    }

    private void getDBInfo(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = database.getReference("museums");

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI

                museumsList = new ArrayList<>();

                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    museumsList.add(snapshot.getValue(Museum.class));
                }

                if (museumsList.size()>0) {
                    mAdapter = new MuseumsRVAdapter(getApplicationContext(), museumsList);
                    museumsRV.setAdapter(mAdapter);
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
        dbRef.addListenerForSingleValueEvent(postListener);

    }
}
