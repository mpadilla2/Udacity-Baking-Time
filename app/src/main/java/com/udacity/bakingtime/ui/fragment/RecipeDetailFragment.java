package com.udacity.bakingtime.ui.fragment;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.constraint.Guideline;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.udacity.bakingtime.R;

import java.util.List;
import java.util.Objects;

public class RecipeDetailFragment extends ViewLifecycleFragment {

    private static final String VIDEO_PLAYER_FRAGMENT = "video_player_fragment";
    private static final String VIDEO_PLAYER_STATE = "video_player_state";
    private static final String RECIPE_INSTRUCTIONS_FRAGMENT = "recipe_instructions_fragment";
    private static final String RECIPE_INSTRUCTIONS_STATE = "recipe_instructions_state";


    FrameLayout recipeInstructionsFrameLayout;
    boolean isLandscape;
    private boolean mIsTwoPaneLayout = false;


    public static RecipeDetailFragment newInstance(){
        RecipeDetailFragment fragment = new RecipeDetailFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecipeDetailFragment() {
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;

        View view = inflater.inflate(R.layout.fragment_recipe_detail, container, false);

        if (Objects.requireNonNull(getActivity()).findViewById(R.id.item_detail_container) != null) {
            mIsTwoPaneLayout = true;
        }

        if (!isLandscape || mIsTwoPaneLayout) {
            recipeInstructionsFrameLayout = view.findViewById(R.id.recipe_step_instructions_fragment);
        }

        Toolbar mToolbar = getActivity().findViewById(R.id.recipe_activity_toolbar);
        // Reference: https://stackoverflow.com/q/42502519/10151438
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(((AppCompatActivity) getActivity()).getSupportActionBar()).setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        launchRecipeDetailFragments();

        return view;
    }


    private void launchRecipeDetailFragments(){

        FragmentManager childFragmentManager = getChildFragmentManager();
        final FragmentTransaction fragmentTransaction = childFragmentManager.beginTransaction();

        Fragment videoPlayerTaggedFragment = childFragmentManager.findFragmentByTag(VIDEO_PLAYER_FRAGMENT);
        Fragment recipeInstructionsTaggedFragment = childFragmentManager.findFragmentByTag(RECIPE_INSTRUCTIONS_FRAGMENT);

        if (videoPlayerTaggedFragment == null){
            videoPlayerTaggedFragment = VideoPlayerFragment.newInstance();
            fragmentTransaction
                    .add(R.id.recipe_step_content_video_player_fragment, videoPlayerTaggedFragment, VIDEO_PLAYER_FRAGMENT)
                    .addToBackStack(VIDEO_PLAYER_STATE);
        } else {
            fragmentTransaction.show(videoPlayerTaggedFragment);
        }


        if (!isLandscape || mIsTwoPaneLayout){

            if ((recipeInstructionsFrameLayout.getVisibility() == View.GONE) || mIsTwoPaneLayout){
                recipeInstructionsFrameLayout.setVisibility(View.VISIBLE);
            }

            if (recipeInstructionsTaggedFragment == null){
                recipeInstructionsTaggedFragment = RecipeInstructionsFragment.newInstance();
                fragmentTransaction
                        .add(R.id.recipe_step_instructions_fragment, recipeInstructionsTaggedFragment, RECIPE_INSTRUCTIONS_FRAGMENT)
                        .addToBackStack(RECIPE_INSTRUCTIONS_STATE);
            } else {
                fragmentTransaction.show(recipeInstructionsTaggedFragment);
            }

        } else {

            if (recipeInstructionsTaggedFragment != null) {
                if (recipeInstructionsTaggedFragment.isVisible()) {
                    fragmentTransaction.hide(recipeInstructionsTaggedFragment);
                }
            }
        }

        fragmentTransaction.commit();
    }
}
