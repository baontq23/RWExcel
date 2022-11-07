package com.baontq.rwexcel;

import com.google.gson.JsonObject;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface APIService {
    @POST("student/import")
    Call<JsonObject> upload(@Body SyncBody body);
}
