package com.example.tiktak;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.tiktak.Models.MediaData;
import com.example.tiktak.Models.MediaObject;
import com.example.tiktak.SearchRecyclerView.ItemAdapter;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<MediaObject> mediaObjects;
    private SearchView searchView;
    private ItemAdapter itemAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchView = findViewById(R.id.searchView);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });

        recyclerView = findViewById(R.id.searchRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mediaObjects = MediaData.mediaObjects;

        itemAdapter = new ItemAdapter(mediaObjects,getApplicationContext());
        recyclerView.setAdapter(itemAdapter);
    }

    private void filterList(String newText) {
        ArrayList<MediaObject> filteredList = new ArrayList<>();
        for (MediaObject media : mediaObjects){
            if (media.getUser_name().toLowerCase().contains(newText.toLowerCase())
                    || media.getTitle().toLowerCase().contains(newText.toLowerCase())
                    || media.getPost_categories().toLowerCase().contains(newText.toLowerCase())){
                filteredList.add(media);
            }
        }
        if (filteredList.isEmpty()){
            Toast.makeText(this,"No data found",Toast.LENGTH_SHORT).show();
        }
        else {
            itemAdapter.setFilteredList(filteredList);
        }
    }
    public void backFromSearch(View view){
        Intent intent = new Intent(SearchActivity.this,HomeActivity.class);
        startActivity(intent);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Animatoo.animateSlideRight(this);
        finish();
    }
}
