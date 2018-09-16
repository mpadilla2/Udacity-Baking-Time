package com.udacity.bakingtime.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Context;

import com.udacity.bakingtime.data.SharedPreferencesUtility;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class RecipeIngredientsUpdateService extends IntentService {

    private static final String ACTION_UPDATE_INGREDIENTS_WIDGETS = "com.udacity.bakingtime.widget.action.UPDATE_INGREDIENTS";
    private static final String RECIPE_ID = "com.udacity.bakingtime.widget.extra.RECIPE_ID";
    private static final String RECIPE_NAME = "com.udacity.bakingtime.widget.extra.RECIPE_NAME";
    private static final String RECIPE_INGREDIENTS = "com.udacity.bakingtime.widget.extra.RECIPE_INGREDIENTS";


    public RecipeIngredientsUpdateService() {
        super("RecipeIngredientsUpdateService");
    }


    /**
     * Starts this service to perform action UPDATE_INGREDIENTS with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionUpdateIngredients(Context context, int recipeId, String recipeName, String recipeIngredients) {
        Intent intent = new Intent(context, RecipeIngredientsUpdateService.class);
        intent.setAction(ACTION_UPDATE_INGREDIENTS_WIDGETS);
        intent.putExtra(RECIPE_ID, recipeId);
        intent.putExtra(RECIPE_NAME, recipeName);
        intent.putExtra(RECIPE_INGREDIENTS, recipeIngredients);
        context.startService(intent);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_INGREDIENTS_WIDGETS.equals(action)) {
                final int param1 = intent.getIntExtra(RECIPE_ID, 0);
                final String param2 = intent.getStringExtra(RECIPE_NAME);
                final String param3 = intent.getStringExtra(RECIPE_INGREDIENTS);
                handleActionUpdateIngredients(param1, param2, param3);
            }
        }
    }


    /**
     * Handle action UPDATE_INGREDIENTS in the provided background thread with the provided
     * parameters.
     */
    private void handleActionUpdateIngredients(int recipeId, String recipeName, String recipeIngredients) {

        // Data is here, passed from the RecipeListFragment;
        // Although widgets cannot have access to sharedpreferences save it away to sharedpreferences.
        SharedPreferencesUtility.getInstance(getApplicationContext()).putData(getApplicationContext(), recipeId, recipeName, recipeIngredients);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, RecipeIngredientsWidgetProvider.class));
        RecipeIngredientsWidgetProvider.updateRecipeIngredientsWidgets(this, appWidgetManager, appWidgetIds);
    }
}
