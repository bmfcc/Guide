package com.iscte.guide;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

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
import com.iscte.guide.models.Item;
import com.iscte.guide.models.Zone;

import java.util.ArrayList;
import java.util.List;

public class ZoneInfo extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ZoneItemsAdapter adapter;
    private List<Item> itemsList;
    private static final String PREFS_NAME = "MyPrefsFile";
    private String mySpace;
    private String language;
    private String zoneId;
    private Zone zone;

    private FirebaseApp app;
    private FirebaseDatabase database;

    private AppInfo appInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zone_info);

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        itemsList = new ArrayList<>();
        adapter = new ZoneItemsAdapter(this, itemsList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME,0);
        mySpace = preferences.getString("current_space", "Default");
        language = preferences.getString("selected_language","Default");
        zoneId = preferences.getString("museumZone", "Default");

        if(zoneId.equals("Default") || zoneId.isEmpty()){
            Toast toast = Toast.makeText(getApplicationContext(), "Not found!", Toast.LENGTH_SHORT);
            toast.show();
            finish();
            return;
        }

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

        getZone();
    }

    private void getZone(){
        FirebaseDatabase db = FirebaseDatabase.getInstance(app);

        DatabaseReference zoneRef = db.getReference("Zones").child(language).child(zoneId);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI

                zone = dataSnapshot.getValue(Zone.class);
                prepareItems();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("GetBDValueError", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        zoneRef.addListenerForSingleValueEvent(postListener);
    }

    private void prepareItems(){
        FirebaseStorage storage;
        FirebaseDatabase db;

        storage = FirebaseStorage.getInstance(app);
        db = FirebaseDatabase.getInstance(app);

        StorageReference storageRef = storage.getReference();

        DatabaseReference itemsRef = db.getReference("Items").child(language);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(String str: zone.getItemsList()){

                    itemsList.add(dataSnapshot.child(str).getValue(Item.class));
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w("GetBDValueError", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        itemsRef.addListenerForSingleValueEvent(postListener);


    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

}