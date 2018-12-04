package com.iscte.guide;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.iscte.guide.models.VisitedZones;
import com.iscte.guide.models.Item;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class ItemInfo extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener{

    private ImageButton startPlaying;
    private ImageView animalImage;
    private TextView tViewDesc;
    private TextView tViewTitle;
    private SeekBar seekBar;
    private MediaPlayer mPlayer = null;
    public static final String PREFS_NAME = "MyPrefsFile";
    private Handler handler;
    private Runnable runnable;

    private Item item = null;

    private DatabaseReference dbRef;
    private DatabaseReference dbStatsRef;

    private FirebaseStorage storage;
    private StorageReference storageRef;
    private StorageReference imagesRef;
    private StorageReference audioRef;

    private String mySpace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_info);
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME,0);

        String stringItem = preferences.getString("museumItem", "Default");

        //get intent para saber o id do museu
        Intent intent = getIntent();
        String itemID= intent.getStringExtra("itemID");

        if(itemID!=null){
            stringItem=itemID;
        }

        if(stringItem.equals("Default")){
            Toast toast = Toast.makeText(getApplicationContext(), "Not found!", Toast.LENGTH_SHORT);
            toast.show();
            finish();
            return;
        }

        String language = preferences.getString("selected_language","Default");
        mySpace = preferences.getString("current_space", "Default");

        if(language.equals("Default")){
            final AlertDialog.Builder dialBuilder1 = new AlertDialog.Builder(this);
            dialBuilder1.setMessage("Language not choosed! Information displayed will appear in English.")
                    .setTitle(R.string.app_name);
            dialBuilder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    dialog.dismiss();
                }
            });
            language="EN";
        }

        FirebaseApp app = FirebaseApp.getInstance(mySpace);
        FirebaseDatabase database = FirebaseDatabase.getInstance(app);
        dbRef = database.getReference("Items").child(language+"/"+stringItem);
        dbStatsRef = database.getReference("Stats");

        storage = FirebaseStorage.getInstance(app);
        storageRef = storage.getReference();
        imagesRef = storageRef.child("Images/Items");
        audioRef = storageRef.child("Audios/"+language);

        getDBInfo();

        setDBInfo(stringItem);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();

                if(mPlayer!=null)
                    mPlayer.stop();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();

        mPlayer.stop();
    }

    private void imageBuild(){
        animalImage = findViewById(R.id.animalImage);

        StorageReference imageReference = imagesRef.child(item.getImageFile());

        GlideApp.with(this).load(imageReference).into(animalImage);

    }

    private void audioBuild(){
        startPlaying = (ImageButton) findViewById(R.id.buttonStartPlay);
        seekBar = (SeekBar) findViewById(R.id.seekBar);

        if(item.getAudioFile()==null || item.getAudioFile().isEmpty()){
            startPlaying.setVisibility(View.INVISIBLE);
            seekBar.setVisibility(View.INVISIBLE);
        }else {
            handler = new Handler();

            mPlayer = new MediaPlayer();
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            StorageReference audioReference = audioRef.child(item.getAudioFile());

            audioReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    try {
                        // Download url of file
                        String url = uri.toString();
                        mPlayer.setDataSource(url);
                        mPlayer.prepare();
                        seekBar.setMax(mPlayer.getDuration());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        mPlayer.seekTo(progress);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });

            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    startPlaying.setImageResource((R.drawable.ic_media_play));
                    mPlayer.seekTo(0);
                    seekBar.setProgress(0);
                }
            });
        }
    }

    public void playCycle(){
        seekBar.setProgress(mPlayer.getCurrentPosition());

        if(mPlayer.isPlaying()){
            Log.w("playCycle","isplaying");
            runnable = new Runnable() {
                @Override
                public void run() {
                    playCycle();
                }
            };
            handler.postDelayed(runnable,1000);
        }
    }

    public void startAudio(View view) {
        if(mPlayer != null && mPlayer.isPlaying()){
            mPlayer.pause();
            ((ImageButton)view).setImageResource((R.drawable.ic_media_play));

        } else if(mPlayer != null){
            mPlayer.start();
            ((ImageButton)view).setImageResource(R.drawable.ic_media_pause);
            playCycle();

        }else{
            try {
                try {
                    mPlayer.prepare();
                }catch (IllegalStateException e){
                    Log.w("app", "ItemInfo - startAudio - prepare() failed");
                }
                mPlayer.start();
                startPlaying.setImageResource(R.drawable.ic_media_pause);
                ((ImageButton)view).setImageResource(R.drawable.ic_media_pause);

            } catch (IOException e) {
                Log.w("app", "ItemInfo - startAudio - prepare() failed");
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
        if(fromUser){
            mPlayer.seekTo(progress);
            seekBar.setProgress(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mPlayer!=null) {
            mPlayer.release();
            handler.removeCallbacks(runnable);
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    public void textBuild(){

        tViewTitle = (TextView) findViewById(R.id.textViewTitle);

        tViewTitle.setText(item.getName());

        tViewDesc = (TextView) findViewById(R.id.textViewDesc);

        tViewDesc.setText(item.getDescription());
        tViewDesc.setMovementMethod(new ScrollingMovementMethod());

    }

    private void getDBInfo(){

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI

                item = dataSnapshot.getValue(Item.class);

                imageBuild();
                audioBuild();
                textBuild();

                ////REVER
                setVisitedZone();
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

    private void setDBInfo(String item){
        DatabaseReference visitsRef = dbStatsRef.child("Visits/"+getDate()+"/"+item);

        visitsRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {

                Integer currentValue = mutableData.getValue(Integer.class);
                if (currentValue == null) {
                    mutableData.setValue(1);
                } else {
                    mutableData.setValue(currentValue + 1);
                }

                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(
                    DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                System.out.println("Transaction completed");
            }
        });
    }

    private String getDate(){
        String date = "";

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH)+1;

        date+=year;

        if(month<10){
            date+="0"+month;
        }else{
            date+=month;
        }

        return date;
    }

    //// REVER
    private void setVisitedZone() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, 0);
        String visitedZones = preferences.getString("visited_zones", "Default");

        VisitedZones visitedZonesObject;

        if (visitedZones.equals("Default")) {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("visited_zones", item.getId());
            editor.commit();

        } else {

            visitedZonesObject = new VisitedZones(visitedZones, mySpace);

            ArrayList<String> vZones = visitedZonesObject.getVisitedZonesArr();

            //CORRIGIR tem de ser adicionado o item mesmo que o array esteja vazio
            if (vZones != null && !vZones.contains(item.getId())) {
                visitedZonesObject.addZone(item.getId(),mySpace);

                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("visited_zones", visitedZonesObject.toString());
                editor.commit();
            }
        }
    }
}
