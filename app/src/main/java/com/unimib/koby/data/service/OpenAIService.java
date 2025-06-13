package com.unimib.koby.data.service;

import com.unimib.koby.BuildConfig;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface OpenAIService {
    String HEADER = "Authorization: Bearer "+ BuildConfig.OPENAI_API_KEY;

    //@Headers({HEADER, "Content-Type: application/json"})
    @POST("https://api.openai.com/v1/chat/completions")
    Call<OpenAIResponse> chatCompletion(@Body OpenAIRequest request);
}