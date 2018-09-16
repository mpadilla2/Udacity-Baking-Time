package com.udacity.bakingtime.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.udacity.bakingtime.R;
import com.udacity.bakingtime.data.SharedPreferencesUtility;
import com.udacity.bakingtime.ui.activity.RecipeActivity;

/**
 * Implementation of App Widget functionality.
 */
public class RecipeIngredientsWidgetProvider extends AppWidgetProvider {

    private static final String RECIPE_ID = "com.udacity.bakingtime.widget.extra.RECIPE_ID";
    private static final String RECIPE_NAME = "com.udacity.bakingtime.widget.extra.RECIPE_NAME";
    private static final String RECIPE_INGREDIENTS = "com.udacity.bakingtime.widget.extra.RECIPE_INGREDIENTS";


    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {

        int recipeId = 0;
        String recipeName;
        String recipeIngredients;
        Intent launchActivityIntent;

        // Grab the recipe info from sharedpreferences
        Bundle bundle = SharedPreferencesUtility.getInstance(context).getData(context);

        recipeId = bundle.getInt(RECIPE_ID, 0);
        recipeName = bundle.getString(RECIPE_NAME, null);
        recipeIngredients = bundle.getString(RECIPE_INGREDIENTS, null);

        if (recipeName == null){
            recipeName = context.getString(R.string.widget_recipe_name_text);
        }

        if (recipeIngredients == null){
            recipeIngredients = context.getString(R.string.widget_recipe_ingredients_text);
        }

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_ingredients_widget);
        views.setTextViewText(R.id.widget_recipe_name_text, recipeName);
        views.setTextViewText(R.id.widget_recipe_ingredients_text, recipeIngredients);

        launchActivityIntent = new Intent(context, RecipeActivity.class);
        launchActivityIntent.putExtra(RECIPE_ID, recipeId);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, launchActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.widget_recipe_name_text, pendingIntent);
        views.setOnClickPendingIntent(R.id.widget_recipe_ingredients_text, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }


    public static void updateRecipeIngredientsWidgets(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){
        for (int appWidgetid : appWidgetIds){
            updateAppWidget(context, appWidgetManager, appWidgetid);
        }

    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        // This method is called on updatePeriodMillis
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }


    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }


    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

