package com.udacity.bakingtime.ui.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.udacity.bakingtime.R;
import com.udacity.bakingtime.data.model.Step;
import com.udacity.bakingtime.data.viewmodel.RecipeViewModel;

import java.util.Objects;

import static com.google.android.exoplayer2.ui.AspectRatioFrameLayout.RESIZE_MODE_FILL;
import static com.google.android.exoplayer2.ui.AspectRatioFrameLayout.RESIZE_MODE_FIT;

// Reference: Exoplayer tutorial: https://codelabs.developers.google.com/codelabs/exoplayer-intro/#2
// Other Reference: https://medium.com/fungjai/playing-video-by-exoplayer-b97903be0b33
// Other Reference: https://android.jlelse.eu/android-exoplayer-starters-guide-6350433f256c
public class VideoPlayerFragment extends ViewLifecycleFragment {

    private static final String PLAYBACK_POSITION = "playback_position";
    private static final String CURRENT_WINDOW_INDEX = "current_window_index";
    private static final String PLAY_WHEN_READY = "play_when_ready";

    private RecipeViewModel mRecipeViewModel;
    private SimpleExoPlayer mExoPlayer;
    private PlayerView mPlayerView;
    private boolean mPlayWhenReady;
    private int mCurrentWindow;
    private long mPlayBackPosition;
    private String mMediaUrl;
    private ImageView mImageView;

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

        if (savedInstanceState != null) {
            mPlayBackPosition = savedInstanceState.getLong(PLAYBACK_POSITION, 0);
            mCurrentWindow = savedInstanceState.getInt(CURRENT_WINDOW_INDEX, 0);
            mPlayWhenReady = savedInstanceState.getBoolean(PLAY_WHEN_READY, false);
        } else {
            mPlayBackPosition = 0;
            mCurrentWindow = 0;
            mPlayWhenReady = true;
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

        View view = inflater.inflate(R.layout.fragment_video_player, container, false);
        final AppBarLayout appBarLayout = getActivity().findViewById(R.id.recipe_activity_app_bar);
        mPlayerView = view.findViewById(R.id.fragment_video_player_playerView);
        mImageView = view.findViewById(R.id.recipe_step_content_imageView);

        if (isLandscape){
            mPlayerView.setResizeMode(RESIZE_MODE_FILL);
            appBarLayout.setVisibility(View.INVISIBLE);
        } else {
            mPlayerView.setResizeMode(RESIZE_MODE_FIT);
            appBarLayout.setVisibility(View.VISIBLE);
        }

        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setUpViewModel();
        loadRecipeStepContent();
    }


    private void setUpViewModel(){
        mRecipeViewModel = ViewModelProviders.of(
                Objects.requireNonNull(getActivity())).get(RecipeViewModel.class);
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // We've already stored necessary exoplayer state in variables, released exoplayer and set it to null.
        if (mExoPlayer != null){
            mPlayBackPosition = mExoPlayer.getCurrentPosition();
            mCurrentWindow = mExoPlayer.getCurrentWindowIndex();
            mPlayWhenReady = mExoPlayer.getPlayWhenReady();

            outState.putLong(PLAYBACK_POSITION, mPlayBackPosition);
            outState.putInt(CURRENT_WINDOW_INDEX, mCurrentWindow);
            outState.putBoolean(PLAY_WHEN_READY, mPlayWhenReady);
        }
    }


    @Override
    public void onPause() {
        super.onPause();
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
    public void onStop() {
        super.onStop();

        releasePlayer();
    }


    private void loadRecipeStepContent(){

        final Observer<Step> stepObserver = new Observer<Step>() {
            @Override
            public void onChanged(@Nullable Step step) {
                if (mExoPlayer != null){
                    releasePlayer();
                }
                // set the mediaUrl for the selected recipe
                if (step.getVideoURL().isEmpty()){
                    if (step.getThumbnailURL().isEmpty()){
                        mMediaUrl = "";
                    } else {
                        mMediaUrl = step.getThumbnailURL();
                    }
                } else {
                    mMediaUrl = step.getVideoURL();
                }

                setUpImageAndPlayerViews();

                Log.d("Mediaurl", "contains: " + mMediaUrl);
            }
        };
        mRecipeViewModel.getSelectedRecipeStep()
                .observe(Objects.requireNonNull(getViewLifecycleOwner()), stepObserver);
    }


    private void setUpImageAndPlayerViews(){

        if (!mMediaUrl.isEmpty()){
            String type = getMimeType(mMediaUrl);

            if (!type.isEmpty()){
                switch (type){
                    case "image":
                        setViewsWhenImage();
                        break;
                    case "video":
                        preparePlayerAndMedia();
                        setViewsWhenVideo();
                        break;
                    default:
                        setViewsWhenNoImageOrVideo();
                        break;
                }
            }
        } else {
            Log.d("Mediaurl", "is empty!");
            setViewsWhenNoImageOrVideo();
        }
    }


    // Reference: https://stackoverflow.com/questions/8589645/how-to-determine-mime-type-of-file-in-android
    @NonNull
    private static String getMimeType(String url) {
        String type;
        String ext = MimeTypeMap.getFileExtensionFromUrl(url);
        if (ext != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
            if (type != null){
                type = type.split("/")[0];
            } else {
                type = "";
            }
        } else {
            type = "";
        }
        return type;
    }


    private void setViewsWhenImage() {
        Glide.with(this)
                .load(mMediaUrl)
                .apply(new RequestOptions().optionalCenterCrop())
                .into(mImageView);
        mPlayerView.setVisibility(View.GONE);
        mImageView.setVisibility(View.VISIBLE);
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
        mExoPlayer.prepare(mediaSource, true, false);
    }


    private void setViewsWhenVideo(){
        mPlayerView.setVisibility(View.VISIBLE);
        mImageView.setVisibility(View.GONE);
    }


    private void setViewsWhenNoImageOrVideo() {
        mImageView.setImageResource(R.drawable.no_video_available_transparent_bg);
        mPlayerView.setVisibility(View.GONE);
        mImageView.setVisibility(View.VISIBLE);
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
    }


    private void releasePlayer(){
        if (mExoPlayer != null){
            mPlayBackPosition = mExoPlayer.getCurrentPosition();
            mCurrentWindow = mExoPlayer.getCurrentWindowIndex();
            mPlayWhenReady = mExoPlayer.getPlayWhenReady();
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }
}
