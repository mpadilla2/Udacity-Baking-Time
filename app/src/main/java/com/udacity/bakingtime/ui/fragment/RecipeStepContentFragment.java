package com.udacity.bakingtime.ui.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

// todo bug onclick next/previous: if next/previous is clicked while playing the video the video keeps playing or the next video starts playing
// todo maintain exoplayer position on rotate

// Reference: Exoplayer tutorial: https://codelabs.developers.google.com/codelabs/exoplayer-intro/#2
// Other Reference: https://medium.com/fungjai/playing-video-by-exoplayer-b97903be0b33
// Other Reference: https://android.jlelse.eu/android-exoplayer-starters-guide-6350433f256c
public class RecipeStepContentFragment extends ViewLifecycleFragment {

    private RecipeViewModel mRecipeViewModel;
    private TextView mStepInstructions;
    private SimpleExoPlayer mExoPlayer;
    private PlayerView mPlayerView;
    private ImageView mImageView;
    private boolean mPlayWhenReady;
    private int mCurrentWindow;
    private long mPlayBackPosition;
    private String mMediaUrl;
    private Button mPreviousButton;
    private Button mNextButton;

    public static RecipeStepContentFragment newInstance(){
        RecipeStepContentFragment fragment = new RecipeStepContentFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecipeStepContentFragment(){
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_recipe_step_content, container, false);
        mPlayerView = view.findViewById(R.id.recipe_step_content_playerView);
        mImageView = view.findViewById(R.id.recipe_step_content_imageView);
        mStepInstructions = view.findViewById(R.id.recipe_step_instructions_textView);
        mPreviousButton = view.findViewById(R.id.recipe_step_content_previous_button);
        mNextButton = view.findViewById(R.id.recipe_step_content_next_button);

        setupButtonClickListeners();
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setUpViewModel();
        loadRecipeStepContent();
        loadButtonObservers();
    }


    @Override
    public void onPause() {
        super.onPause();

        if (Util.SDK_INT <= 23){
            releasePlayer();
        }
    }


    @Override
    public void onStop() {
        super.onStop();

        if (Util.SDK_INT > 23){
            releasePlayer();
        }
    }

    private void setUpViewModel(){
        mRecipeViewModel = ViewModelProviders.of(
                Objects.requireNonNull(getActivity())).get(RecipeViewModel.class);
    }


    private void loadRecipeStepContent(){

        final Observer<Step> stepObserver = new Observer<Step>() {
            @Override
            public void onChanged(@Nullable Step step) {
                mStepInstructions.setText(Objects.requireNonNull(step).getDescription());

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


    private void loadButtonObservers(){

        final Observer<Boolean> prevButtonAtBegin = new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean aBoolean) {
                if (Objects.requireNonNull(aBoolean).equals(Boolean.TRUE)){
                    mPreviousButton.setEnabled(false);
                } else {
                    mPreviousButton.setEnabled(true);
                }
            }
        };
        mRecipeViewModel.getStepsBeginReached()
                .observe(Objects.requireNonNull(getViewLifecycleOwner()), prevButtonAtBegin);

        final Observer<Boolean> nextButtonAtEnd = new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean bBoolean) {
                if (Objects.requireNonNull(bBoolean).equals(Boolean.TRUE)){
                    mNextButton.setEnabled(false);
                } else {
                    mNextButton.setEnabled(true);
                }
            }
        };
        mRecipeViewModel.getStepsEndReached()
                .observe(getViewLifecycleOwner(), nextButtonAtEnd);

        mRecipeViewModel.setButtonsStatus();
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
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mStepInstructions.getLayoutParams();
        params.topToBottom = mPlayerView.getId();
        mStepInstructions.setLayoutParams(params);
    }


    private void setViewsWhenNoImageOrVideo() {
        mImageView.setImageResource(R.drawable.no_video_available_transparent_bg);
        mPlayerView.setVisibility(View.GONE);
        mImageView.setVisibility(View.VISIBLE);
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) mStepInstructions.getLayoutParams();
        params.topToBottom = mImageView.getId();
        mStepInstructions.setLayoutParams(params);
    }


    private void setupButtonClickListeners(){

        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecipeViewModel.getPreviousRecipeStep();
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecipeViewModel.getNextRecipeStep();
            }
        });
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
