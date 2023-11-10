package com.example.tiktak.MainRecyclerView;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.example.tiktak.Models.MediaData;
import com.example.tiktak.Models.MediaObject;
import com.example.tiktak.R;

import java.util.ArrayList;

public class VideoPlayerRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<MediaObject> mediaObjects;
    private Context context;
    private RequestManager requestManager;

    public VideoPlayerRecyclerAdapter(ArrayList<MediaObject> mediaObjects, Context context,RequestManager requestManager) {
        this.mediaObjects = mediaObjects;
        this.context = context;
        this.requestManager = requestManager;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new com.example.tiktak.MainRecyclerView.VideoPlayerViewHolder(
                LayoutInflater.from(context).inflate(R.layout.layout_main,viewGroup,false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ((com.example.tiktak.MainRecyclerView.VideoPlayerViewHolder)viewHolder).onBind(mediaObjects.get(i),requestManager);
    }

    @Override
    public int getItemCount() {
        return mediaObjects.size();
    }

}
