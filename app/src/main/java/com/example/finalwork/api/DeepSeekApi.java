package com.example.finalwork.api;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Streaming;

public interface DeepSeekApi {
    @POST("chat/completions")
    Call<DeepSeekResponse> chat(@Header("Authorization") String authorization, @Body DeepSeekRequest request);

    @Streaming
    @POST("chat/completions")
    Call<ResponseBody> chatStream(@Header("Authorization") String authorization, @Body DeepSeekRequest request);
}
