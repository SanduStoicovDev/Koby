package com.unimib.koby.ui.newchat;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.unimib.koby.data.repository.chat.ChatRepository;
import com.unimib.koby.data.repository.chat.OpenAIRepository;
import com.unimib.koby.model.Chat;
import com.unimib.koby.model.ChatMessage;
import com.unimib.koby.model.Result;

import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.text.PDFTextStripper;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ViewModel che gestisce la logica di una nuova chat:
 *  • salvataggio PDF scelto
 *  • chiamate OpenAI (riassunto o semplice chat)
 *  • LiveData per messaggi, loading, risultato/errore
 */
public class NewChatViewModel extends ViewModel {

    private final ChatRepository chatRepo = new ChatRepository();
    private final OpenAIRepository openAI  = new OpenAIRepository();
    private final ExecutorService io       = Executors.newSingleThreadExecutor();

    /** Messaggi mostrati nella conversazione */
    private final MutableLiveData<List<ChatMessage>> messages = new MutableLiveData<>(new ArrayList<>());
    public LiveData<List<ChatMessage>> getMessages() { return messages; }

    /** Stato di caricamento (progress bar) */
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    public LiveData<Boolean> getLoading() { return loading; }

    /** Risultato finale (successo / errore) */
    private final MutableLiveData<Result> result = new MutableLiveData<>();
    public LiveData<Result> getResult() { return result; }

    /** PDF selezionato ma non ancora inviato */
    private InputStream pendingPdf;

    // -----------------------------------------------------
    // Public API
    // -----------------------------------------------------

    public void setPendingPdf(InputStream pdf) {
        this.pendingPdf = pdf;
    }

    public void send(String userPrompt) {
        appendMessage(new ChatMessage("user", userPrompt));
        loading.postValue(true);

        io.execute(() -> {
            try {
                String answer;
                if (pendingPdf != null) {
                    // -------- Riassunto PDF --------
                    String text;
                    try (PDDocument doc = PDDocument.load(pendingPdf)) {
                        text = new PDFTextStripper().getText(doc);
                    }
                    answer = openAI.summarize(text);
                    pendingPdf = null; // consumato
                } else {
                    // -------- Chat semplice --------
                    answer = openAI.chat(userPrompt);
                }

                ChatMessage assistant = new ChatMessage("assistant", answer);
                chatRepo.createChat(new Chat(userPrompt, answer), Arrays.asList(
                        new ChatMessage("user", userPrompt), assistant));

                appendMessage(assistant);
                result.postValue(new Result.Success(answer));

            } catch (Exception e) {
                Log.e("NewChatViewModel", "OpenAI call failed", e);
                result.postValue(new Result.Error(e.getMessage()));
            } finally {
                loading.postValue(false);
            }
        });
    }

    // -----------------------------------------------------
    // Helpers
    // -----------------------------------------------------

    private void appendMessage(ChatMessage m) {
        List<ChatMessage> cur = new ArrayList<>(messages.getValue());
        cur.add(m);
        messages.postValue(cur);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        io.shutdownNow();
    }
}
