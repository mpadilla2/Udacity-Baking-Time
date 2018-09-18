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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.exoplayer2.C;
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


// Reference: Exoplayer tutorial: https://codelabs.developers.google.com/codelabs/exoplayer-intro/#2
// Other Reference: https://medium.com/fungjai/playing-video-by-exoplayer-b97903be0b33
// Other Reference: https://android.jlelse.eu/android-exoplayer-starters-guide-6350433f256c
public class VideoPlayerFragment extends ViewLifecycleFragment{

    private static final String PLAYBACK_POSITION = "playback_position";
    private static final String CURRENT_WINDOW_INDEX = "current_window_index";
    private static final String PLAY_WHEN_READY = "play_when_ready";
    public static final String INDEX = "index";

    private RecipeViewModel mRecipeViewModel;
    private SimpleExoPlayer mExoPlayer;
    private PlayerView mPlayerView;
    private boolean mPlayWhenReady;
    private int mCurrentWindow;
    private long mPlayBackPosition;
    private String mMediaUrl;
    private String mThumbnailUrl;
    private TextView mTextView;
    private boolean isLandscape;
    private boolean mIsLargeScreen;
    private AppBarLayout mAppBarLayout;



    public static VideoPlayerFragment newInstance(int index){
        VideoPlayerFragment fragment = new VideoPlayerFragment();
        Bundle args = new Bundle();
        args.putInt(INDEX, index);
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

        int mInitialStepIndex = getArguments() != null ? getArguments().getInt(INDEX) : 0;

        if (savedInstanceState != null){
            mPlayBackPosition = savedInstanceState.getLong(PLAYBACK_POSITION, 0);
            mCurrentWindow = savedInstanceState.getInt(CURRENT_WINDOW_INDEX);
            mPlayWhenReady = savedInstanceState.getBoolean(PLAY_WHEN_READY, false);
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

        // Reference: https://stackoverflow.com/questions/35237549/change-layoutmanager-depending-on-device-format
        mIsLargeScreen = Objects.requireNonNull(getActivity()).getResources().getBoolean(R.bool.isLargeScreen);

        View view = inflater.inflate(R.layout.fragment_video_player, container, false);
        mPlayerView = view.findViewById(R.id.fragment_video_player_playerView);
        mTextView = view.findViewById(R.id.recipe_step_content_textView);
        mAppBarLayout = getActivity().findViewById(R.id.recipe_activity_app_bar);

        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (!mIsLargeScreen) {

            if (isLandscape) {
                hideSystemUI();
            }

            // Reference: https://developer.android.com/training/system-ui/visibility
            mPlayerView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    // Note that system bars will only be "visible" if none of the
                    // LOW_PROFILE, HIDE_NAVIGATION, or FULLSCREEN flags are set.
                    if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        // System bars are visible
                        if (mMediaUrl.isEmpty()) {
                            mTextView.setVisibility(View.VISIBLE);
                        }
                    } else {
                        // System bars are NOT visible
                        if (mAppBarLayout.getVisibility() == View.VISIBLE) {
                            mAppBarLayout.setVisibility(View.GONE);
                        }
                    }
                }
            });
        }

        setUpViewModel();
        loadRecipeStepContent();
    }


    private void setUpViewModel(){
        mRecipeViewModel = ViewModelProviders.of(
                Objects.requireNonNull(getActivity())).get(RecipeViewModel.class);

        VideoPlayerViewModel mVideoPlayerViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(VideoPlayerViewModel.class);
    }


    @Override
    public void onPause() {
        super.onPause();

        if (mExoPlayer != null) {
            mPlayBackPosition = mExoPlayer.getCurrentPosition();
            mCurrentWindow = mExoPlayer.getCurrentWindowIndex();
            mPlayWhenReady = mExoPlayer.getPlayWhenReady();
        }
    }


    @Override
    public void onStop() {
        super.onStop();

        releasePlayer();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

        releasePlayer();
    }


    @Override
    public void onResume() {
        super.onResume();

        if (!mMediaUrl.isEmpty() && mPlayBackPosition > 0 && mExoPlayer != null){
            loadRecipeStepContent();
            mExoPlayer.seekTo(mPlayBackPosition);
            mExoPlayer.setPlayWhenReady(mPlayWhenReady);
        }
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putLong(PLAYBACK_POSITION, mPlayBackPosition);
        outState.putInt(CURRENT_WINDOW_INDEX, mCurrentWindow);
        outState.putBoolean(PLAY_WHEN_READY, mPlayWhenReady);
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

                if (!Objects.requireNonNull(step).getVideoURL().isEmpty()) {
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

                    Glide.with(Objects.requireNonNull(getContext()))
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
        mExoPlayer.prepare(mediaSource, false, false);
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

        mPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
        mPlayerView.setPlayer(mExoPlayer);
        mExoPlayer.setPlayWhenReady(mPlayWhenReady);
        mExoPlayer.seekTo(mCurrentWindow, mPlayBackPosition);
        mExoPlayer.setVideoScalingMode(C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
    }


    private void releasePlayer(){
        if (mExoPlayer != null){

            mExoPlayer.release();
            mExoPlayer = null;
        }
    }


    private void hideSystemUI() {
        // Reference: https://developer.android.com/training/system-ui/immersive
        mPlayerView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
}
