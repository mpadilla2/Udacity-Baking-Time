package com.udacity.bakingtime.ui.activity;

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

import com.udacity.bakingtime.ui.fragment.RecipeListFragment;


// Todo - BUG exoplayer fills container now but looks squished in portrait view and stretched in landscape
// Todo - BUG recipe steps are in cardview?? Don't remember applying that but needs to be removed
// Todo - BUG Exoplayer rotation needs to correctly continue the video at proper position
// Todo - format all text to use Roboto and match material design guidelines
// Todo - format margins, padding, borders to match mocks
// Todo - proper network check before grabbing info - use intent service this time!
// Todo - add homescreen widget that displays ingredient list for selected recipe
// Todo - Add to Widget in the app itself in an already selected recipe
// Todo - Espresso tests of the UI
// Todo - extract all dimensions, strings
// Todo - clean code
// Todo - review rubric, mocks to see if missed anything

// DONE - BUG in portrait mode video exoplayer starts lower than the app bar leaving a white space on top;
//      to troubleshoot I added background colors to all possible objects involved and determined it's the
//      playerview that's taller than the exoplayer screen, leaving the space on top
// DONE - BUG phone landscape mode fullscreen video: swipe to get the controls then backpress,
//      actionbar DOES show but has a margin top and left skewing its placement
//      while testing noticed that in landscape mode the actionbar loads skewed if the video is there
//      Exact same problem as this: https://stackoverflow.com/questions/28874114/action-bar-displayed-incorrectly-when-returning-from-immersive-mode

public class RecipeActivity extends AppCompatActivity{

    private static final String RECIPE_LIST_FRAGMENT = "recipe_list_tag";
    boolean isLandscape;
    Toolbar mToolbar;
    AppBarLayout mAppBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recipe);

        isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        mToolbar = findViewById(R.id.recipe_activity_toolbar);
        mAppBarLayout = findViewById(R.id.recipe_activity_app_bar);

        if (savedInstanceState != null){
            Log.d("RECIPEACTIVITY", "SAVEDINSTANCESTATE EXISTS");
            return;
        }

        loadRecipeList();
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (getSupportFragmentManager().getBackStackEntryCount() == 0){
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            setTitle(R.string.app_name);
        }

        if (isLandscape) {
            showSystemUI();
        }
    }


    private void showSystemUI(){
        mAppBarLayout.setVisibility(View.VISIBLE);
    }
}

