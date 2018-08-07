package com.udacity.bakingtime.data.remote;

import com.udacity.bakingtime.data.model.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiClient {
    @GET("topher/2017/May/59121517_baking/baking.json")
    Call<List<Recipe>> getRecipes();
}

