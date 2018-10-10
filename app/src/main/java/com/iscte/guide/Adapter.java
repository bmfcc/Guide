package com.iscte.guide;

/**
 * Created by b.coitos on 5/28/2018.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class Adapter extends PagerAdapter {

    private ArrayList<String> images;
    private LayoutInflater inflater;
    private Context context;

    private String mySpace;

    private FirebaseStorage storage;
    private StorageReference storageRef;
    private StorageReference imagesRef;

    public static final String PREFS_NAME = "MyPrefsFile";


    public Adapter(Context context, ArrayList<String> images, String mySpace) {
        this.context = context;
        this.images=images;
        this.mySpace=mySpace;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {
        View myImageLayout = inflater.inflate(R.layout.slide, view, false);
        ImageView myImage = myImageLayout.findViewById(R.id.image);

        getStorageInstance();

        //storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        imagesRef = storageRef.child("Images").child("Slide");

        StorageReference imageReference = imagesRef.child(images.get(position));

        GlideApp.with(view).load(imageReference).into(myImage);

        view.addView(myImageLayout, 0);
        return myImageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    private void getStorageInstance(){

        FirebaseApp firebaseApp = FirebaseApp.getInstance(mySpace);

        storage = FirebaseStorage.getInstance(firebaseApp);

    }
}
