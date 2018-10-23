package com.iscte.guide;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.iscte.guide.models.Museum;

import java.util.ArrayList;

public class MuseumInfoActivity extends AppCompatActivity {

    private Museum museum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_museum_info);

        //get intent para saber o id do museu
        Intent intent = getIntent();
        String museumID= intent.getStringExtra("museumID");

        getDBInfo(museumID);
    }

    public void submitClicked(View v){

    }

    public void locationButtonClicked(View v){
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(museum.getLocation()));
        startActivity(intent);
    }

    public void appButtonClicked(View v){
        Intent intent = new Intent(this,Main2Activity.class);
        intent.putExtra("limited",true);
        startActivity(intent);
    }

    private void getDBInfo(String museumID){

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference museumRef = database.getReference("museums/" + museumID);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                museum = dataSnapshot.getValue(Museum.class);
                prepareActivity();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("GetBDValueError", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        museumRef.addListenerForSingleValueEvent(postListener);

    }

    private void prepareActivity(){
        ((TextView)findViewById(R.id.museumName)).setText(museum.getName());
        ((TextView)findViewById(R.id.priceValueTV)).setText(museum.getPrice());
        ((TextView)findViewById(R.id.addressValueTV)).setText(museum.getAddress());
        ((TextView)findViewById(R.id.hoursValueTV)).setText(museum.getSchedule());

        RatingBar curRating = findViewById(R.id.currentRatingRB);
        curRating.setRating(museum.getRating());
    }
}
