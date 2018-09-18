package com.udacity.bakingtime.ui.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.udacity.bakingtime.R;

import android.support.v7.widget.Toolbar;
import android.view.View;

import com.udacity.bakingtime.data.SharedPreferencesUtility;
import com.udacity.bakingtime.data.model.Recipe;
import com.udacity.bakingtime.data.viewmodel.RecipeViewModel;
import com.udacity.bakingtime.ui.fragment.RecipeIngredientStepFragment;
import com.udacity.bakingtime.ui.fragment.RecipeListFragment;

import java.util.List;
import java.util.Objects;


public class RecipeActivity extends AppCompatActivity{

    private static final String RECIPE_LIST_FRAGMENT = "recipe_list_tag";
    private static final String RECIPE_ID = "com.udacity.bakingtime.widget.extra.RECIPE_ID";
    private static final String RECIPE_INGREDIENT_STEP_FRAGMENT = "recipe_ingredient_step_fragment";
    private static final String RECIPE_LIST_STATE = "recipe_list_state";
    private boolean isLandscape;
    private Toolbar mToolbar;
    private AppBarLayout mAppBarLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recipe);

        isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        mToolbar = findViewById(R.id.recipe_activity_toolbar);
        mAppBarLayout = findViewById(R.id.recipe_activity_app_bar);


        RecipeViewModel mRecipeViewModel = ViewModelProviders.of(this).get(RecipeViewModel.class);

        final Observer<List<Recipe>> recipeListObserver = new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipes) {
            }
        };
        mRecipeViewModel.getAllRecipes()
                .observe(Objects.requireNonNull(this), recipeListObserver);

        if (savedInstanceState == null) {

            // Read in from shared preferences.
            // if app is started from widget, there may be a selected recipe in shared preferences
            // Create a Recipe object
            // set the selected recipe using the created recipe object
            // If had to create object, then launchRecipeIngredientsSteps
            // else loadRecipeList

            Recipe recipe = SharedPreferencesUtility.getInstance(this).getData();

            if (recipe != null){
                mRecipeViewModel.setSelectedRecipe(recipe);
                launchRecipeIngredientsSteps();
            } else {
                loadRecipeList();
            }
        }
    }


    private void loadRecipeList() {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment taggedFragment = fragmentManager.findFragmentByTag(RECIPE_LIST_FRAGMENT);

        // Not adding first fragment to backstack because I want the app to exit onbackpress with last fragment.
        if (taggedFragment == null) {
            RecipeListFragment recipeListFragment = RecipeListFragment.newInstance();
            fragmentTransaction
                    .add(R.id.activity_fragment_container, recipeListFragment, RECIPE_LIST_FRAGMENT);
        } else {
            fragmentTransaction.show(taggedFragment);
        }

        fragmentTransaction.commit();
    }


    private void launchRecipeIngredientsSteps(){

        FragmentManager fragmentManager = Objects.requireNonNull(getSupportFragmentManager());
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Fragment taggedFragment = fragmentManager.findFragmentByTag(RECIPE_INGREDIENT_STEP_FRAGMENT);

        if (taggedFragment == null){
            taggedFragment = RecipeIngredientStepFragment.newInstance();
            fragmentTransaction
                    .add(R.id.activity_fragment_container, taggedFragment, RECIPE_INGREDIENT_STEP_FRAGMENT)
                    .addToBackStack(RECIPE_LIST_STATE);
        } else {
            fragmentTransaction.show(taggedFragment);
        }

        List<Fragment> fragmentList = fragmentManager.getFragments();

        for (Fragment fragment : fragmentList){
            if (!fragment.equals(taggedFragment) && fragment.isVisible()){
                fragmentTransaction.hide(fragment);
            }
        }

        fragmentTransaction.commit();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (getSupportFragmentManager().getBackStackEntryCount() == 0){
            Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            mToolbar.setTitle(R.string.app_name);
            loadRecipeList();
        }

        if (isLandscape) {
            showSystemUI();
        }
    }


    private void showSystemUI(){
        mAppBarLayout.setVisibility(View.VISIBLE);
    }


    // When in singleTop launchmode, the intent coming back from widget will call onNewIntent.
    // The selected recipe is already set from the user's click into a recipe within the app.
    // Reference: https://developer.android.com/guide/topics/manifest/activity-element#lmode
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        int mRecipeId = intent.getIntExtra(RECIPE_ID, 0);

        if (mRecipeId > 0){
            launchRecipeIngredientsSteps();
        }
    }
}

