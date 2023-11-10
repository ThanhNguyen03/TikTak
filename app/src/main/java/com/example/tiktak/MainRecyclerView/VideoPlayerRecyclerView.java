package com.example.tiktak.MainRecyclerView;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.Uri;
import android.se.omapi.Session;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.tiktak.Models.MediaData;
import com.example.tiktak.Models.MediaObject;
import com.example.tiktak.R;
import com.google.android.exoplayer2.ExoPlaybackException;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class VideoPlayerRecyclerView extends RecyclerView {
    private static final String TAG = "VideoPlayerRecyclerView";
    private enum VolumeState{ON,OFF};

    //////UI
    private ImageView thumbnail,volumeControl,soundDisc;
    private ProgressBar progressBar;
    private View viewHolderParent;
    private FrameLayout frameLayout;
    private PlayerView videoSurfaceView;
    private SimpleExoPlayer videoPlayer;
    private TextView musicName, description, username;
    private String itemLink;

    //////Vars
    private ArrayList<MediaObject> mediaObjects = new ArrayList<>();
    private int videoSurfaceDefaultHeight = 0;
    private int screenDefaultHeight = 0;
    private Context context;
    private int playPosition = -1;
    private boolean isVideoViewAdded;
    private RequestManager requestManager;
    private CircleImageView profileBtn;

    //////Controlling playback state
    private VolumeState volumeState;

    public VideoPlayerRecyclerView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public VideoPlayerRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context){

        this.context = context.getApplicationContext();
        Display display = ((WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        videoSurfaceDefaultHeight = point.x;
        screenDefaultHeight = point.y;

        videoSurfaceView = new PlayerView(this.context);
        videoSurfaceView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        //////Create the player
        videoPlayer = ExoPlayerFactory.newSimpleInstance(context,trackSelector);
        //////Bind the player to the view
        videoSurfaceView.setUseController(false);
        videoSurfaceView.setPlayer(videoPlayer);
        setVolumeControl(VolumeState.ON);

        addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                    Log.d(TAG,"onScrollStateChanged: called.");
                    if (thumbnail != null){
                        ////show old thumbnail
                        thumbnail.setVisibility(VISIBLE);
                    }
                    ////
                    if (!recyclerView.canScrollVertically(1)){
                        playVideo(true);
                    }else {
                        playVideo(false);
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        addOnChildAttachStateChangeListener(new OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {

            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {
                if (viewHolderParent != null && viewHolderParent.equals(view)){
                    resetVideoView();
                }
            }
        });

        videoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                switch (playbackState){
                    case Player.STATE_BUFFERING:
                        Log.e(TAG,"onPlayerStateChanged: Buffering video.");
                        if (progressBar!=null){
                            progressBar.setVisibility(VISIBLE);
                        }
                        break;

                    case Player.STATE_ENDED:
                        Log.d(TAG,"onPlayerStateChanged: Video ended.");
                        videoPlayer.seekTo(0);
                        break;

                    case Player.STATE_IDLE:
                        break;

                    case Player.STATE_READY:
                        Log.e(TAG,"onPlayerStateChanged: Ready to play.");
                        if (progressBar!=null){
                            progressBar.setVisibility(GONE);
                        }
                        if (!isVideoViewAdded){
                            addVideoView();
                        }
                        break;

                    default:
                        break;
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });
    }
    public void playVideo(boolean isEndOfList){
        int targetPosition  ;

        if (!isEndOfList){
            int startPosition = ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
            int endPosition = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();

            ////if more than 2 lists items on screen, set the difference to be 1
            if (endPosition - startPosition>1){
                endPosition = startPosition +1;
            }
            ////something is wrong. return
            if (startPosition<0||endPosition<0){
                return;
            }
            ////if more than 1 lists items on screen
            if (startPosition!=endPosition){
                int startPositionVideoHeight = getVisibleVideoSurfaceHeight(startPosition);
                int endPositionVideoHeight = getVisibleVideoSurfaceHeight(endPosition);

                targetPosition = startPositionVideoHeight > endPositionVideoHeight ? startPosition : endPosition;
            }
            else {
                targetPosition = startPosition;
            }
        }
        else {
            targetPosition = mediaObjects.size()-1;
        }

        Log.d(TAG,"playVideo: target position: "+targetPosition);
        ////Video is already playing so return
        if (targetPosition==playPosition){
            return;
        }
        ////Set the position of the list item that is to be played
        playPosition = targetPosition;
        if (videoSurfaceView==null){
            return;
        }
        ////Remove any old surface views from previously playing videos
        videoSurfaceView.setVisibility(INVISIBLE);
        removeVideoView(videoSurfaceView);

        int currentPosition = targetPosition - ((LinearLayoutManager)getLayoutManager()).findLastVisibleItemPosition();

        View child = getChildAt(currentPosition);
        if (child ==null){
            return;
        }
        VideoPlayerViewHolder holder = (VideoPlayerViewHolder) child.getTag();
        if (holder==null){
            playPosition=-1;
            return;
        }

        soundDisc = holder.soundDisc;
        thumbnail = holder.thumbnail;
        progressBar = holder.progressBar;
        volumeControl = holder.volumeControl;
        viewHolderParent = holder.itemView;
        requestManager = holder.requestManager;
        frameLayout = holder.itemView.findViewById(R.id.media_container);
        musicName = holder.music_name;
        username = holder.user_name;
        description = holder.description;

        Glide.with(context).load(R.drawable.vinyl).into(soundDisc);

        ///////////////////////////////////////////////////
        videoSurfaceView.setPlayer(videoPlayer);
        viewHolderParent.setOnClickListener(videoViewClickListener);

        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(context, Util.getUserAgent(context," RecyclerView VideoPlayer"));
        String mediaUrl = mediaObjects.get(targetPosition).getMedia_url();

        if (mediaUrl != null){
            SharedPreferences sharedPreferences = context.getSharedPreferences("user_name", MODE_PRIVATE);
            sharedPreferences.edit().putString("username", mediaObjects.get(targetPosition).getUser_name()).apply();

            MediaSource videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(mediaUrl));
            videoPlayer.prepare(videoSource);
            videoPlayer.setPlayWhenReady(true);
        }
        //////////////////////////////////////////////////////

    }
    private OnClickListener videoViewClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            toggleVolume();
        }
    };
    private int getVisibleVideoSurfaceHeight(int playPosition){
        int at = playPosition - ((LinearLayoutManager)getLayoutManager()).findFirstVisibleItemPosition();
        Log.d(TAG,"getVisibleVideoSurfaceHeight: at: "+at);

        View child = getChildAt(at);
        if (child==null){
            return 0;
        }
        int[] location = new int[2];
        child.getLocationInWindow(location);

        if (location[1]<0){
            return location[1]+videoSurfaceDefaultHeight;
        }else {
            return screenDefaultHeight-location[1];
        }
    }
    ////Remove the old player
    private void removeVideoView(PlayerView videoView){
        ViewGroup parent = (ViewGroup) videoView.getParent();
        if (parent==null){
            return;
        }
        int index = parent.indexOfChild(videoView);
        if (index>=0){
            parent.removeViewAt(index);
            isVideoViewAdded = false;
            viewHolderParent.setOnClickListener(null);
        }
    }

    public void addVideoView(){
        frameLayout.addView(videoSurfaceView);
        isVideoViewAdded = true;
        videoSurfaceView.requestFocus();
        videoSurfaceView.setVisibility(VISIBLE);
        videoSurfaceView.setAlpha(1);
        thumbnail.setVisibility(GONE);
    }

    private void resetVideoView(){
        if (isVideoViewAdded){
            removeVideoView(videoSurfaceView);
            playPosition = -1;
            videoSurfaceView.setVisibility(INVISIBLE);
            thumbnail.setVisibility(VISIBLE);
        }
    }

    public void releasePlayer(){
        if (videoPlayer!= null){
            videoPlayer.release();
            videoPlayer = null;
        }
        viewHolderParent = null;
    }

    private void toggleVolume(){
        if (videoPlayer!=null){
            if (volumeState==VolumeState.OFF){
                Log.d(TAG,"togglePlaybackState: enabling volume.");
                setVolumeControl(VolumeState.ON);
            }else if (volumeState ==VolumeState.ON){
                Log.d(TAG,"togglePlaybackState: disabling volume.");
                setVolumeControl(VolumeState.OFF);
            }
        }
    }

    private void setVolumeControl(VolumeState state){
        volumeState = state;
        if (state==VolumeState.OFF){
            videoPlayer.setVolume(0f);
            animateVolumeControl();
        } else if (state==VolumeState.ON) {
            videoPlayer.setVolume(1f);
            animateVolumeControl();
        }
    }

    private void animateVolumeControl(){
        if (volumeControl!=null){
            volumeControl.bringToFront();
            if (volumeState == VolumeState.OFF){
                requestManager.load(R.drawable.volume_off).into(volumeControl);
            } else if (volumeState == VolumeState.ON) {
                requestManager.load(R.drawable.volume_up).into(volumeControl);
            }
            volumeControl.animate().cancel();
            volumeControl.setAlpha(1f);
            volumeControl.animate().alpha(0f).setDuration(600).setStartDelay(1000);

        }
    }
    public void setMediaObjects(ArrayList<MediaObject> mediaObjects){
        this.mediaObjects = mediaObjects;
    }

    public void saveVideoPlayerState() {
        if (videoPlayer != null) {
            boolean isPlaying = videoPlayer.getPlayWhenReady();
            long currentWindow = videoPlayer.getCurrentWindowIndex();
            long playbackPosition = Math.max(0, videoPlayer.getContentPosition());

            SharedPreferences sharedPreferences = context.getSharedPreferences("video_player_state", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("isPlaying", isPlaying);
            editor.putLong("currentWindow", currentWindow);
            editor.putLong("playbackPosition", playbackPosition);
            editor.apply();
        }
    }
    public void restoreVideoPlayerState() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("video_player_state", MODE_PRIVATE);
        boolean isPlaying = sharedPreferences.getBoolean("isPlaying", false);
        long currentWindow = sharedPreferences.getLong("currentWindow", 0);
        long playbackPosition = sharedPreferences.getLong("playbackPosition", 0);

        if (videoPlayer != null) {
            videoPlayer.setPlayWhenReady(isPlaying);
            videoPlayer.seekTo((int)currentWindow, playbackPosition);
        }
    }
}
