package com.udacity.bakingtime.ui.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.res.Configuration;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.udacity.bakingtime.R;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.udacity.bakingtime.data.viewmodel.RecipeViewModel;
import com.udacity.bakingtime.ui.fragment.RecipeIngredientStepFragment;
import com.udacity.bakingtime.ui.fragment.RecipeListFragment;

import java.util.List;
import java.util.Objects;

// Todo - Bug - home screen widget. if empty and click widget recipeId is ALWAYS 1
// Todo - Bug - home screen widget. if I've selected a recipe and then click the widget, get null pointer exception.
// Todo - Bug - think I need singleTop
// Todo - home screen widget - preview image
// Todo - Espresso tests of the UI
// Todo - extract all dimensions, strings
// Todo - clean code
// Todo - review rubric, mocks to see if missed anything


// DONE - add homescreen widget that displays ingredient list for desired recipe
// Widget 1:13:30 - https://www.youtube.com/watch?v=gHEYLqO1Zao
// Reference: https://developer.android.com/guide/topics/appwidgets/overview
// Reference: https://developer.android.com/guide/topics/appwidgets/
// * per mentor Bruno F.: "You do not need to display a list of the recipes on the widget neither ask them for selecting anything from there, you should be able to get the last clicked project and display the ingredients for that recipe. =)"
//   based on the above answer from Bruno F.,
//   when user selects a recipe in RecipeListFragment CustomItemClickListener, send the selected recipe name and ingredient formatted string to the widget.
//   in the widget, receive the data and display the recipe name and ingredients
// * choose the right widget: textviews should suffice
// * resizable? Yes
// * orientation/device independent, layout capable of stretching and contracting.
// * previewImage - after it's working add a widget; configure it;
//      then take a screenshot to save as a previewImage
// * will need to store the previously selected recipe and ingredients from the widget. shared preferences should work since it's very minimal data.
// DONE - BUG widget, when first added should display some default text but right now only displays a blank blue screen
// DONE - home screen widget - allow click back into ingredient list fragment


public class RecipeActivity extends AppCompatActivity{

    private static final String RECIPE_LIST_FRAGMENT = "recipe_list_tag";
    private static final String RECIPE_ID = "com.udacity.bakingtime.widget.extra.RECIPE_ID";
    private static final String RECIPE_INGREDIENT_STEP_FRAGMENT = "recipe_ingredient_step_fragment";
    private static final String RECIPE_LIST_STATE = "recipe_list_state";
    boolean isLandscape;
    Toolbar mToolbar;
    AppBarLayout mAppBarLayout;
    int mRecipeId;
    private RecipeViewModel mRecipeViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recipe);

        mRecipeViewModel = ViewModelProviders.of(this).get(RecipeViewModel.class);

        isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        mToolbar = findViewById(R.id.recipe_activity_toolbar);
        mAppBarLayout = findViewById(R.id.recipe_activity_app_bar);

        mRecipeId = getIntent().getIntExtra(RECIPE_ID, 0);
        // todo bug recipeid is ALWAYS coming in as 1 WHY?
        if (mRecipeId != 0){
            mRecipeViewModel.setSelectedRecipeId(mRecipeId);
            launchRecipeIngredientsSteps();
        } else {
            loadRecipeList();
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
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            mToolbar.setTitle(R.string.app_name);
        }

        if (isLandscape) {
            showSystemUI();
        }
    }


    private void showSystemUI(){
        mAppBarLayout.setVisibility(View.VISIBLE);
    }
}

