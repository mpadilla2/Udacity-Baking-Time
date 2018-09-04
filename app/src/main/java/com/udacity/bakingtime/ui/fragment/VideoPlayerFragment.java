package com.udacity.bakingtime.ui.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.udacity.bakingtime.R;
import com.udacity.bakingtime.data.model.Step;
import com.udacity.bakingtime.data.viewmodel.RecipeViewModel;
import com.udacity.bakingtime.data.viewmodel.VideoPlayerViewModel;

import java.util.Objects;


// onpause
// onsaveinstancestate
// onstop
// ondestroy
// oncreate
// onviewcreated
// onactivitycreated
// onstart
// onresume


// Reference: Exoplayer tutorial: https://codelabs.developers.google.com/codelabs/exoplayer-intro/#2
// Other Reference: https://medium.com/fungjai/playing-video-by-exoplayer-b97903be0b33
// Other Reference: https://android.jlelse.eu/android-exoplayer-starters-guide-6350433f256c
public class VideoPlayerFragment extends ViewLifecycleFragment {

    private static final String PLAYBACK_POSITION = "playback_position";
    private static final String CURRENT_WINDOW_INDEX = "current_window_index";
    private static final String PLAY_WHEN_READY = "play_when_ready";

    private RecipeViewModel mRecipeViewModel;
    private VideoPlayerViewModel mVideoPlayerViewModel;
    private SimpleExoPlayer mExoPlayer;
    private PlayerView mPlayerView;
    private boolean mPlayWhenReady;
    private int mCurrentWindow;
    private long mPlayBackPosition;
    private String mMediaUrl;
    private String mThumbnailUrl;
    private TextView mTextView;
    boolean isLandscape;
    View decorView;
    private boolean mIsLargeScreen;



    public static VideoPlayerFragment newInstance(){
        VideoPlayerFragment fragment = new VideoPlayerFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public VideoPlayerFragment(){
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("VideoPlayerFragment", "ONCREATE");

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        Log.d("VideoPlayerFragment", "ONCREATEVIEW");

        isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

        // Reference: https://stackoverflow.com/questions/35237549/change-layoutmanager-depending-on-device-format
        mIsLargeScreen = Objects.requireNonNull(getActivity()).getResources().getBoolean(R.bool.isLargeScreen);
        decorView = getActivity().getWindow().getDecorView();

        View view = inflater.inflate(R.layout.fragment_video_player, container, false);
        final AppBarLayout appBarLayout = getActivity().findViewById(R.id.recipe_activity_app_bar);
        mPlayerView = view.findViewById(R.id.fragment_video_player_playerView);
        mTextView = view.findViewById(R.id.recipe_step_content_textView);

        if (!mIsLargeScreen) {
            if (isLandscape) {
                hideSystemUI();
            } else {
                showSystemUI();
            }

            // Reference: https://developer.android.com/training/system-ui/visibility
            decorView.setOnSystemUiVisibilityChangeListener
                    (new View.OnSystemUiVisibilityChangeListener() {
                        @Override
                        public void onSystemUiVisibilityChange(int visibility) {
                            // Note that system bars will only be "visible" if none of the
                            // LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
                            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                                appBarLayout.setVisibility(View.VISIBLE);
                                mTextView.setVisibility(View.VISIBLE);
                                mPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH);
                            } else {
                                appBarLayout.setVisibility(View.GONE);
                                mPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
                            }
                        }
                    });
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d("VideoPlayerFragment", "ONSTART");

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d("VideoPlayerFragment", "ONACTIVITYCREATED");

        setUpViewModel();
        loadRecipeStepContent();
    }


    private void setUpViewModel(){
        mRecipeViewModel = ViewModelProviders.of(
                Objects.requireNonNull(getActivity())).get(RecipeViewModel.class);

        mVideoPlayerViewModel = ViewModelProviders.of(
                Objects.requireNonNull(getActivity())).get(VideoPlayerViewModel.class);
    }


    @Override
    public void onPause() {
        super.onPause();
        Log.d("VideoPlayerFragment", "ONPAUSE");

        releasePlayer();
    }


    @Override
    public void onResume() {
        super.onResume();

        Log.d("VideoPlayerFragment", "ONRESUME");

        if (!mMediaUrl.isEmpty() && mPlayBackPosition > 0 && mExoPlayer != null){
            loadRecipeStepContent();
            mExoPlayer.seekTo(mPlayBackPosition);
            mExoPlayer.setPlayWhenReady(mPlayWhenReady);
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        Log.d("VideoPlayerFragment", "ONSTOP");

        releasePlayer();
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        Log.d("VideoPlayerFragment", "ONSAVEINSTANCESTATE");

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.d("VideoPlayerFragment", "ONDESTROY");

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d("VideoPlayerFragment", "ONVIEWCREATED");
    }


    private void loadRecipeStepContent(){

        final Observer<Step> stepObserver = new Observer<Step>() {
            @Override
            public void onChanged(@Nullable Step step) {

                mMediaUrl = "";
                mThumbnailUrl = "";

                if (mExoPlayer != null) {
                    releasePlayer();
                }

                if (!step.getVideoURL().isEmpty()) {
                    mMediaUrl = step.getVideoURL();
                }

                if (!step.getThumbnailURL().isEmpty()){
                    mThumbnailUrl = step.getThumbnailURL();
                }

                if (mMediaUrl.isEmpty()){
                    mPlayerView.setVisibility(View.GONE);
                    mTextView.setVisibility(View.VISIBLE);
                } else {
                    mTextView.setVisibility(View.GONE);
                    mPlayerView.setVisibility(View.VISIBLE);

                    Glide.with(getContext())
                            .asBitmap()
                            .load(mThumbnailUrl)
                            .apply(new RequestOptions().fitCenter())
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    mPlayerView.setDefaultArtwork(resource);
                                }
                            });

                    preparePlayerAndMedia();
                }
            }
        };

        mRecipeViewModel.getSelectedRecipeStep()
                .observe(Objects.requireNonNull(getViewLifecycleOwner()), stepObserver);
    }



    private void preparePlayerAndMedia(){
        if (mExoPlayer != null) {
            mExoPlayer.stop();
        } else {
            initializePlayer();
        }

        String userAgent = Util.getUserAgent(getContext(), getString(R.string.app_name));
        MediaSource mediaSource = new ExtractorMediaSource.Factory(
                new DefaultHttpDataSourceFactory(userAgent))
                .createMediaSource(Uri.parse(mMediaUrl));
        mExoPlayer.prepare(mediaSource);
    }


    /*
     * Reference: https://codelabs.developers.google.com/codelabs/exoplayer-intro/#2
     * "Roughly a RenderersFactory creates renderers for timestamp synchronized rendering of video, audio and text (subtitles).
     * The TrackSelector is responsible for selecting from the available audio, video and text tracks and the LoadControl manages buffering
     * of the player."
     */
    private void initializePlayer(){
        mExoPlayer = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(getContext()),
                new DefaultTrackSelector(),
                new DefaultLoadControl());

        mPlayerView.setPlayer(mExoPlayer);
        mExoPlayer.setPlayWhenReady(mPlayWhenReady);
        mExoPlayer.seekTo(mCurrentWindow, mPlayBackPosition);
        //mExoPlayer.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
    }


    private void releasePlayer(){
        if (mExoPlayer != null){

            mVideoPlayerViewModel.setCurrentWindow(mExoPlayer.getCurrentWindowIndex());
            mVideoPlayerViewModel.setPlayBackPosition(mExoPlayer.getContentPosition());
            mVideoPlayerViewModel.setPlayWhenReady(mExoPlayer.getPlayWhenReady());
            mVideoPlayerViewModel.setMediaUrl(mMediaUrl);

            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }


    private void hideSystemUI() {
        // Enables regular "immersive" mode.
        // Reference: https://developer.android.com/training/system-ui/immersive
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }


    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        //decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }
}
