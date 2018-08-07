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

// Todo BUG ingredients textview is not showing the first 3 ingredients
//      it's like they are behind the toolbar
// Todo BUG recipe steps cuts off the first step: Recipe Introduction on all recipes except first one.
//      The difference between first recipe and the others is
//          all of the steps fit in the screen for the first recipe.
// Todo BUG on rotate recipe steps dissapear; title changes from recipe name to baking time.
    // on rotate back the recipe steps come back, but the title stays at baking time.
    // Test: onactivitycreated contains:
    //        code to populate recyclerview
    //        code to set the toolbar title
    //        this method fires ever time I rotate the phone
    //        so why does the recyclerview populate sometimes but not others?
    //        why does the toolbar not get set again?
// Todo add recipe ingredients to recipe ingredient step fragment
// Todo set up recipe step content with media player showing video thumbnail or image
// Todo set up recipe step content with recipe step instructions
// Todo set up recipe step content with previous and next step instructions
// Todo set up phone landscape view after portrait is finished
// Todo set up tablet views after phone views are finished

// DONE BUG on scroll recipe steps become wider and wider apart


/*
 * In RecipeStepContentFragment:
 * Get the selectedRecipeStep variable from the viewmodel,
 *      Display the recipe step content for the selected recipe step
 */


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

