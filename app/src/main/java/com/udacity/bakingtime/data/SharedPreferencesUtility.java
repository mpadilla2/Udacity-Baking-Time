package com.udacity.bakingtime.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

// Reference: https://developer.android.com/reference/android/content/SharedPreferences
// Reference: https://developer.android.com/training/data-storage/shared-preferences
// Reference: https://gist.github.com/chintansoni202/f1767fbe68f7f897cfb3096e8cd83480
public class SharedPreferencesUtility {

    private static volatile SharedPreferencesUtility mSharedPreferencesUtility;
    private Context mContext;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mSharedPreferencesEditor;

    private static final String PREF_FILENAME = "widget_preferences";
    private static final String RECIPE_ID = "com.udacity.bakingtime.widget.extra.RECIPE_ID";
    private static final String RECIPE_NAME = "com.udacity.bakingtime.widget.extra.RECIPE_NAME";
    private static final String RECIPE_INGREDIENTS = "com.udacity.bakingtime.widget.extra.RECIPE_INGREDIENTS";



    private SharedPreferencesUtility(Context context){
        mContext = context;
        mSharedPreferences = context.getSharedPreferences(PREF_FILENAME, Context.MODE_PRIVATE);
        mSharedPreferencesEditor = mSharedPreferences.edit();
    }


    // Reference: https://medium.com/exploring-code/how-to-make-the-perfect-singleton-de6b951dfdb0
    public static SharedPreferencesUtility getInstance(Context context){

        // Double check locking pattern
        if (mSharedPreferencesUtility == null){ // first check

            synchronized (SharedPreferencesUtility.class){ // second check
                if (mSharedPreferencesUtility == null) mSharedPreferencesUtility = new SharedPreferencesUtility(context);
            }

        }

        return mSharedPreferencesUtility;
    }


    public void putData(Context context, int recipeId, String recipeName, String recipeIngredients){

        mSharedPreferencesEditor.putInt(RECIPE_ID, recipeId);
        mSharedPreferencesEditor.putString(RECIPE_NAME, recipeName);
        mSharedPreferencesEditor.putString(RECIPE_INGREDIENTS, recipeIngredients);
        mSharedPreferencesEditor.apply();
    }


    public Bundle getData(Context context){

        Bundle bundle = new Bundle();
        bundle.putInt(RECIPE_ID, mSharedPreferences.getInt(RECIPE_ID, 1));
        bundle.putString(RECIPE_NAME, mSharedPreferences.getString(RECIPE_NAME, null));
        bundle.putString(RECIPE_INGREDIENTS, mSharedPreferences.getString(RECIPE_INGREDIENTS, null));

        return bundle;
    }
}
