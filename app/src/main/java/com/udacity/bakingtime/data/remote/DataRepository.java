package com.udacity.bakingtime.data.remote;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.annotation.NonNull;
import android.util.Log;

import com.udacity.bakingtime.data.model.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DataRepository {

    private final ApiClient mApiClient;

    public DataRepository(Application application){
        mApiClient = ApiUtils.getApi();
    }


    public LiveData<List<Recipe>> getAllRecipes(){

        final MutableLiveData<List<Recipe>> recipes = new MutableLiveData<>();

        mApiClient.getRecipes().enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(@NonNull Call<List<Recipe>> call, @NonNull Response<List<Recipe>> response) {
                if (response.isSuccessful()){
                    if (response.body() != null) {
                        recipes.setValue(response.body());
                        Log.d("DATAREPOSITORY", "LOADED RECIPES FOR FROM INTERNET API");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Recipe>> call, @NonNull Throwable t) {
                Log.d("DATAREPOSITORY", "LOADING RECIPES FROM INTERNET API FOR FAILED");
            }
        });
        return recipes;
    }
}

