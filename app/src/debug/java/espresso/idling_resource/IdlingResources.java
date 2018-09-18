package espresso.idling_resource;


import android.support.test.espresso.IdlingRegistry;

import com.jakewharton.espresso.OkHttp3IdlingResource;

import okhttp3.OkHttpClient;

// Reference: https://wajahatkarim.com/2018/06/idling-registry-for-okhttp/
public abstract class IdlingResources {

    public static void registerOkHttp(OkHttpClient client){
        IdlingRegistry.getInstance().register(OkHttp3IdlingResource.create("okhttp", client));
    }
}
