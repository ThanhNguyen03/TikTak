package com.example.tiktak;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.example.tiktak.MainRecyclerView.VerticalSpacingItemDecorator;
import com.example.tiktak.MainRecyclerView.VideoPlayerRecyclerAdapter;
import com.example.tiktak.MainRecyclerView.VideoPlayerRecyclerView;
import com.example.tiktak.Models.MediaData;
import com.example.tiktak.Models.MediaObject;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private ArrayList<MediaObject> mediaObjectList = new ArrayList<>();
    private VideoPlayerRecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();
    }
    private void init(){

        if(Build.VERSION.SDK_INT >=19 && Build.VERSION.SDK_INT <21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if(Build.VERSION.SDK_INT<19){
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
        if (Build.VERSION.SDK_INT>=21){
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        /////////////////RECYCLERVIEW/////////////

        recyclerView = (VideoPlayerRecyclerView)findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        VerticalSpacingItemDecorator itemDecorator = new VerticalSpacingItemDecorator(0);
        recyclerView.addItemDecoration(itemDecorator);

        ///////////////////////////////////////////////////////
        SnapHelper mSnapHelper = new PagerSnapHelper();
        mSnapHelper.attachToRecyclerView(recyclerView);
        ///////////////////////////////////////////////////////
        LoadAdapter();

    }
    private void LoadAdapter(){
        mediaObjectList =  MediaData.mediaObjects;
        recyclerView.setMediaObjects(mediaObjectList);

        VideoPlayerRecyclerAdapter adapter = new VideoPlayerRecyclerAdapter(mediaObjectList,HomeActivity.this,initGlide());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        recyclerView.setKeepScreenOn(true);
        recyclerView.smoothScrollToPosition(mediaObjectList.size()+1);
    }
    public  static void setWindowFlag(@NotNull Activity activity, final int bits, boolean on){
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on){
            winParams.flags |= bits;
        }else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
    private RequestManager initGlide(){
        RequestOptions options = new RequestOptions().placeholder(R.color.colorDarkGrey).error(R.color.colorRed);
        return Glide.with(this).setDefaultRequestOptions(options);
    }
    @Override
    protected void onDestroy() {
        if (recyclerView!=null){
            recyclerView.releasePlayer();
        }
        super.onDestroy();
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (recyclerView!=null){
            recyclerView.releasePlayer();
            recyclerView.saveVideoPlayerState();
        }
        finish();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    public void followingBtn(View view){
        Intent intent = new Intent(HomeActivity.this,FollowingActivity.class);
        startActivity(intent);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Animatoo.animateSwipeRight(this);
        finish();
    }
    public void searchBtn(View view){
        Intent intent = new Intent(HomeActivity.this,SearchActivity.class);
        startActivity(intent);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Animatoo.animateSlideUp(this);
        finish();
    }
    public void profileBtn(View view){
        Intent intent = new Intent(HomeActivity.this,ProfileActivity.class);
        startActivity(intent);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Animatoo.animateSlideUp(this);
        finish();
    }

    public void userBtn(View view){
        Intent intent = new Intent(HomeActivity.this,UserActivity.class);
        startActivity(intent);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Animatoo.animateSlideLeft(this);
        finish();
    }
    public void resetHome(View view){
        MediaData.LoadData();
        Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
        startActivity(intent);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Animatoo.animateShrink(this);
        finish();
    }
    public void cameraButton(View view){
        Intent intent = new Intent(HomeActivity.this, CameraActivity.class);
        startActivity(intent);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
    }
}