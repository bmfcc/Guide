package com.iscte.guide;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.iscte.guide.models.Museum;

import java.util.ArrayList;

/**
 * Created by b.coitos on 10/19/2018.
 */

public class MuseumsRVAdapter extends RecyclerView.Adapter<MuseumsRVAdapter.ViewHolder>{

    private ArrayList<Museum> museumsList;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View view;
        public ImageView image;
        public TextView name;
        public TextView address;
        public TextView schedule;
        public String museumID;


        public ViewHolder(View v) {
            super(v);

            view = v;
            this.image = v.findViewById(R.id.museumIV);
            this.name = v.findViewById(R.id.musemNameTV);
            this.address = v.findViewById(R.id.museumAddressTV);
            this.schedule = v.findViewById(R.id.museumcheduleTV);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(),MuseumInfoActivity.class);
                    intent.putExtra("museumID",museumID);
                    view.getContext().startActivity(intent);
                }
            });
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MuseumsRVAdapter(Context context, ArrayList<Museum> museumsList) {
        this.museumsList = museumsList;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MuseumsRVAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.museums, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        Museum museum = museumsList.get(position);

        if(museum.getImage()!=null && !museum.getImage().isEmpty()) {
            StorageReference imageRef = storageRef.child("Images/Museums").child(museum.getImage());
            GlideApp.with(context).load(imageRef).into(holder.image);
        }else{
            holder.image.setVisibility(View.INVISIBLE);
        }
        holder.name.setText(museum.getName());
        holder.address.setText(museum.getAddress());
        holder.schedule.setText(museum.getSchedule());
        holder.museumID = museum.getId();

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return museumsList.size();
    }
}