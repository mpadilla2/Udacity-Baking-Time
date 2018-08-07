package com.udacity.bakingtime.data.remote;

public class ApiUtils {

    private static final String BASE_URL = "https://d17h27t6h515a5.cloudfront.net/";

    public static ApiClient getApi() {
        return RetrofitClient
                .getRetrofitClient(BASE_URL)
                .create(ApiClient.class);
    }
}