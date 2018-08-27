package com.udacity.bakingtime.ui.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.udacity.bakingtime.R;
import com.udacity.bakingtime.data.model.Step;
import com.udacity.bakingtime.data.viewmodel.RecipeViewModel;

import java.util.Objects;


public class RecipeStepContentFragment extends ViewLifecycleFragment {

    private RecipeViewModel mRecipeViewModel;
    private TextView mStepInstructions;
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


    private void setUpViewModel(){
        mRecipeViewModel = ViewModelProviders.of(
                Objects.requireNonNull(getActivity())).get(RecipeViewModel.class);
    }


    private void loadRecipeStepContent(){

        final Observer<Step> stepObserver = new Observer<Step>() {
            @Override
            public void onChanged(@Nullable Step step) {
                mStepInstructions.setText(Objects.requireNonNull(step).getDescription());
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
}
