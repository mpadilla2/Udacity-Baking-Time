package com.udacity.bakingtime.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.gson.Gson;
import com.udacity.bakingtime.data.model.Recipe;

import java.util.List;

// Reference: https://developer.android.com/reference/android/content/SharedPreferences
// Reference: https://developer.android.com/training/data-storage/shared-preferences
// Reference: https://gist.github.com/chintansoni202/f1767fbe68f7f897cfb3096e8cd83480
public class SharedPreferencesUtility {

    private static volatile SharedPreferencesUtility mSharedPreferencesUtility;
    private Context mContext;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mSharedPreferencesEditor;

    private static final String PREF_FILENAME = "widget_preferences";
    private static final String SELECTED_RECIPE = "com.udacity.bakingtime.widget.extra.SELECTED_RECIPE";

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


    // Reference: https://www.javacodegeeks.com/2013/12/storing-objects-in-android.html
    public void putData(Context context, Recipe recipe){
        Gson gson = new Gson();
        String recipe_json = gson.toJson(recipe);
        mSharedPreferencesEditor.putString(SELECTED_RECIPE, recipe_json);
        mSharedPreferencesEditor.apply();
    }


    public Recipe getData(){
        Gson gson = new Gson();
        String recipe_json = mSharedPreferences.getString(SELECTED_RECIPE, "");
        return gson.fromJson(recipe_json, Recipe.class);
    }
}
