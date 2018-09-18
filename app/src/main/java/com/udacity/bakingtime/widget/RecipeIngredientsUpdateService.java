package com.udacity.bakingtime.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Context;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class RecipeIngredientsUpdateService extends IntentService {


    public RecipeIngredientsUpdateService() {
        super("RecipeIngredientsUpdateService");
    }


    /**
     * Starts this service to perform action UPDATE_INGREDIENTS with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionUpdateIngredients(Context context) {
        handleActionUpdateIngredients(context);
    }


    @Override
    protected void onHandleIntent(Intent intent) {
    }


    private static void handleActionUpdateIngredients(Context context) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, RecipeIngredientsWidgetProvider.class));
        RecipeIngredientsWidgetProvider.updateRecipeIngredientsWidgets(context, appWidgetManager, appWidgetIds);
    }
}
