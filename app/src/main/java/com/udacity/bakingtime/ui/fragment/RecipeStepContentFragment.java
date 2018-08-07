package com.udacity.bakingtime.ui.fragment;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.bakingtime.R;
import com.udacity.bakingtime.data.viewmodel.RecipeViewModel;

import java.util.Objects;

/* todo bug!
 * RecipeList -> RecipeIngredientStep -> RecipeStepContent
 *
 * onbackpress from recipeStepContent:
 * OCCURS:      Both RecipeList AND RecipeIngredientStep show
 * EXPECTED:    Only RecipeIngredientStep should show
 */

public class RecipeStepContentFragment extends Fragment {

    private RecipeViewModel mRecipeViewModel;
    //private Step mRecipeStep = new Step();


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




        return super.onCreateView(inflater, container, savedInstanceState);
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


    private void loadRecipeStepContent(){
        final String shortDescription = Objects.requireNonNull(mRecipeViewModel.getSelectedRecipeStep().getValue()).getShortDescription();
    }
}
