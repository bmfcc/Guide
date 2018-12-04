package com.iscte.guide;

/**
 * Created by b.coitos on 11/8/2018.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.FirebaseApp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.iscte.guide.models.Item;

import java.util.List;


public class ZoneItemsAdapter extends RecyclerView.Adapter<ZoneItemsAdapter.MyViewHolder>{

    private Context mContext;
    private List<Item> itemsList;
    private static final String PREFS_NAME = "MyPrefsFile";


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView thumbnail;
        public String itemId;
        public View view;

        public MyViewHolder(View v) {
            super(v);
            this.view=v;
            title = (TextView) view.findViewById(R.id.title);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);

            /*view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(),ItemInfo.class);
                    intent.putExtra("itemID",itemId);
                    view.getContext().startActivity(intent);
                }
            });*/
        }
    }

    public ZoneItemsAdapter(Context mContext, List<Item> albumList) {
        this.mContext = mContext;
        this.itemsList = albumList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.zone_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Item item = itemsList.get(position);
        holder.title.setText(item.getName());
        holder.itemId = item.getId();

        SharedPreferences preferences = mContext.getSharedPreferences(PREFS_NAME,0);
        String mySpace = preferences.getString("current_space", "Default");

        FirebaseApp app;
        FirebaseStorage storage;

        if(mySpace!="Default") {
            app = FirebaseApp.getInstance(mySpace);
        }else{
            app = FirebaseApp.getInstance();
        }

        storage = FirebaseStorage.getInstance(app);

        StorageReference storageRef = storage.getReference();

        if(item.getImageFile()!=null){
            StorageReference imageRef = storageRef.child("Images/Items").child(item.getImageFile());
            Glide.with(mContext).load(imageRef).into(holder.thumbnail);
        }

        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),ItemInfo.class);
                intent.putExtra("itemID",holder.itemId);
                holder.view.getContext().startActivity(intent);
            }
        });
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(),ItemInfo.class);
                intent.putExtra("itemID",holder.itemId);
                holder.view.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }
}
