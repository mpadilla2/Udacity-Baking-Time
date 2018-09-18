package com.udacity.bakingtime.data.remote;

import espresso.idling_resource.IdlingResources;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    public static Retrofit getRetrofitClient(String baseUrl){

        OkHttpClient httpClient = new OkHttpClient();
        IdlingResources.registerOkHttp(httpClient);

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();
    }
}
