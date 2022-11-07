package com.baontq.rwexcel;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServerAPI {
    public static final String SERVER_ENTRYPOINT = "https://api.easyedu.online/";
    public static final String LOCAL_ENTRYPOINT = "https://api.easyedu.online/";
    public static Retrofit retrofit;

    public static Retrofit getInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(LOCAL_ENTRYPOINT)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
