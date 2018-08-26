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

// Todo format ingredients display text to be similar to recyclerview text display
// Todo set up phone landscape view after portrait is finished
// Todo set up tablet views after phone views are finished

// Todo BUGS IN PHONE LANDSCAPE MODE
// Todo BUG on rotate recipe title changes from recipe name to baking time; on rotate back the title stays at baking time.
// Test: onactivitycreated contains:
//        code to populate recyclerview
//        code to set the toolbar title
//        this method fires every time I rotate the phone
//        so why does the recyclerview populate sometimes but not others?
//        why does the toolbar not get set again?

public class RecipeActivity extends AppCompatActivity{

    private static final String RECIPE_LIST_FRAGMENT = "recipe_list_tag";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        setToolBarTitle();

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
            RecipeListFragment recipeListFragment = RecipeListFragment.newInstance(1);
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
            setToolBarTitle();
        }
    }


    private void setToolBarTitle() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.recipe_activity_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
    }
}

