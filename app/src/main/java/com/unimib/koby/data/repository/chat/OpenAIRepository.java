package com.unimib.koby.data.repository.chat;

import android.util.Log;

import com.unimib.koby.BuildConfig;
import com.unimib.koby.data.service.OpenAIRequest;
import com.unimib.koby.data.service.OpenAIResponse;
import com.unimib.koby.data.service.OpenAIService;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OpenAIRepository {

    private static final String TAG = "OpenAI";                    // Logcat tag
    private final OpenAIService api;

    public OpenAIRepository() {

        /* -------- OkHttp con due interceptor -------- */
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
        // 1) interceptor custom: aggiunge Authorization + log minimale
                .addInterceptor(chain -> {
                    Request orig = chain.request();

                    Request newReq = orig.newBuilder()
                            .addHeader("Authorization", "Bearer " + BuildConfig.OPENAI_API_KEY)
                            .build();

                    Log.d(TAG, "URL  → " + newReq.url().encodedPath());
                    Log.d(TAG, "Auth header len = " +
                            newReq.header("Authorization").length());   // niente chiave in chiaro

                    return chain.proceed(newReq);
                })

                // 2) interceptor di logging corpo/header (OkHttp)
                .addInterceptor(new HttpLoggingInterceptor(
                        message -> Log.d(TAG, message))    // delega il print a Logcat
                        .setLevel(HttpLoggingInterceptor.Level.BODY))   // log completo
                .build();

        /* -------- Retrofit -------- */
        api = new Retrofit.Builder()
                .baseUrl("https://api.openai.com/")          // baseUrl obbligatorio con '/'
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OpenAIService.class);
    }

    /** Chiamata di riepilogo: bloccante, da invocare su thread background. */
    public String summarize(String pdfText) throws IOException {
        String prompt = "Scrivi un riassunto dettagliato del seguente testo. Includi tutti i punti principali, dati scientifici e concetti importanti, evitando semplificazioni eccessive:\\n" + pdfText;

        OpenAIRequest req = new OpenAIRequest();
        req.model = "gpt-4o";
        req.messages = Collections.singletonList(new OpenAIRequest.Message("user", prompt));

        OpenAIResponse resp = api.chatCompletion(req).execute().body();

        if (resp != null && resp.choices != null && !resp.choices.isEmpty()) {
            return resp.choices.get(0).message.content;
        }
        throw new IOException("Risposta vuota da OpenAI");
    }

    /** Chat “generica”: dato un prompt restituisce la risposta dell’assistente. */
    public String chat(String userPrompt) throws IOException {

        OpenAIRequest req = new OpenAIRequest();
        req.model = "gpt-4o";   // usa lo stesso modello di summarize
        req.messages = Collections.singletonList(
                new OpenAIRequest.Message("user", userPrompt));

        OpenAIResponse resp = api.chatCompletion(req).execute().body();

        if (resp != null && resp.choices != null && !resp.choices.isEmpty()) {
            return resp.choices.get(0).message.content;
        }
        throw new IOException("Risposta vuota da OpenAI");
    }
}