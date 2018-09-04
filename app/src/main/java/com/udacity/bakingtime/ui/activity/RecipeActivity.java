package com.udacity.bakingtime.ui.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.udacity.bakingtime.R;

import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.udacity.bakingtime.ui.fragment.RecipeListFragment;

import java.util.Objects;

// Todo - BUG If I am in landscape and viewing a video then pull the navigation tools down, the textview displays and shows "no video" in addition to the video showing.
// Todo - BUG in Tablet mode, if I back press while on a step, it doesn't return to recipe list, instead the step detail disappears.
// Todo - BUG in Tablet mode, if I rotate from portrait to landscape, I have no toolbar available and everything goes to fullscreen; still need toolbar
// Todo - BUG in Tablet mode, need to keep the toolbar saying Baking App and not the recipe name; also no up navigation
// Todo - BUG in portrait mode video playerview starts lower than the app bar leaving a white space on top
// Todo - BUG when in landscape mode if I swipe to get the controls and then backpress, the recipe steps are hidden partially
// under the controls and the toolbar doesn't display all the way. Also, if I click a step in this landscape mode the app crashes.
// The navigation buttons also appear white
// Todo - BUG Exoplayer rotation needs to correctly continue the video at proper position
// Todo - BUG app crashes if I navigate all the way down into recipe detail and then back to recipe list. Back navigation of the same works fine.
/*
    1. Click a Recipe > Recipe step shows
	2. Click a Recipe Step > recipe detail shows
	3. Click up navigation > Recipe Step shows
	4. Click up navigation > App crashes

    I think the RecipeDetailFragment is still lingering and so when I click up navigation in recipe step then it's actually clicking the recipedetailfragment up navigation

	Process: com.udacity.bakingtime, PID: 28968
    java.lang.NullPointerException: Attempt to invoke virtual method 'void android.support.v4.app.FragmentActivity.onBackPressed()' on a null object reference
        at com.udacity.bakingtime.ui.fragment.RecipeDetailFragment$1.onClick(RecipeDetailFragment.java:57)
        at android.view.View.performClick(View.java:5637)
        at android.view.View$PerformClick.run(View.java:22429)
        at android.os.Handler.handleCallback(Handler.java:751)
        at android.os.Handler.dispatchMessage(Handler.java:95)
        at android.os.Looper.loop(Looper.java:154)
        at android.app.ActivityThread.main(ActivityThread.java:6119)
        at java.lang.reflect.Method.invoke(Native Method)
        at com.android.internal.os.ZygoteInit$MethodAndArgsCaller.run(ZygoteInit.java:886)
        at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:776)
 */


// Todo - proper network check before grabbing info - use intent service this time!
// Todo - add homescreen widget that displays ingredient list for selected recipe
// Todo - Add to Widget in the app itself in an already selected recipe

// Todo Espresso tests of the UI
// Todo format ingredients display text to be similar to recyclerview text display
// Todo set up tablet views after phone views are finished


// Todo extract all dimensions, strings
// Todo clean code
// Todo review rubric, mocks to see if missed anything


public class RecipeActivity extends AppCompatActivity{

    private static final String RECIPE_LIST_FRAGMENT = "recipe_list_tag";
    private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recipe);

        setUpToolbar();

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
            setUpToolbar();
            mToolbar.setTitle(getTitle());
        }
    }

    private void setUpToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.recipe_activity_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
    }
}

