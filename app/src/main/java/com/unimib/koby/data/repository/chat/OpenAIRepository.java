package com.unimib.koby.data.repository.chat;

import android.util.Log;

import androidx.annotation.WorkerThread;

import com.unimib.koby.BuildConfig;
import com.unimib.koby.data.openai.OpenAIRequest;
import com.unimib.koby.data.openai.OpenAIResponse;
import com.unimib.koby.data.openai.OpenAIService;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.io.IOException;
import java.util.Collections;

public class OpenAIRepository {
    private final OpenAIService api;

    public OpenAIRepository(){
        api = new Retrofit.Builder()
                .baseUrl("https://api.openai.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OpenAIService.class);
    }

    private static final String TAG = "OpenAIRepository";

    @WorkerThread
    public String summarize(String documentText) throws IOException {
        String prompt = "Riassumi il seguente documento:\n"+documentText;
        OpenAIRequest request = new OpenAIRequest();
        request.messages = Collections.singletonList(new OpenAIRequest.Message("user",prompt));
        OpenAIResponse resp = api.chatCompletion(request).execute().body();
        Log.d("OpenAI", "API key? len=" + BuildConfig.OPENAI_API_KEY.length());
        if (resp==null || resp.choices==null || !resp.choices.isEmpty()) {
            Log.e(TAG, "OpenAI HTTP " + resp.choices );
        }
        if(resp!=null && resp.choices!=null && !resp.choices.isEmpty()){
            return resp.choices.get(0).message.content;
        }
        throw new IOException("Risposta vuota da OpenAI");
    }
}