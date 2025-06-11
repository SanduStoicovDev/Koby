package com.unimib.koby.ui.newchat;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.unimib.koby.model.Chat;
import com.unimib.koby.model.ChatMessage;
import com.unimib.koby.data.repository.chat.ChatRepository;
import com.unimib.koby.data.repository.chat.OpenAIRepository;
import com.unimib.koby.model.Result;

import com.tom_roush.pdfbox.pdmodel.PDDocument;
import com.tom_roush.pdfbox.text.PDFTextStripper;

import java.io.InputStream;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class NewChatViewModel extends ViewModel {
    private final OpenAIRepository openAI = new OpenAIRepository();
    private final ChatRepository chatRepo = new ChatRepository();
    private final MutableLiveData<Result> result = new MutableLiveData<>();
    private final ExecutorService io = Executors.newSingleThreadExecutor();

    public LiveData<Result> getResult() { return result; }

    private static final String TAG = "NewChatVM";

    public void createChatFromPdf(InputStream pdfStream) {
        io.execute(() -> {
            try {
                // 1. Carica PDF e logga info
                String text;
                try (PDDocument doc = PDDocument.load(pdfStream)) {
                    Log.d(TAG, "PDF caricato. Pagine: " + doc.getNumberOfPages());
                    text = new PDFTextStripper().getText(doc);
                }
                Log.d(TAG, "Lunghezza testo estratto: " + text.length() + " caratteri");

                // 2. Chiamata OpenAI
                String summary = openAI.summarize(text);
                Log.d(TAG, "Riassunto generato. Token stimati: " + summary.length()/4);

                // 3. Salvataggio Firestore
                Chat chat = new Chat("Riassunto PDF", summary);
                ChatMessage userMsg = new ChatMessage("user", "[PDF caricato]");
                ChatMessage assistantMsg = new ChatMessage("assistant", summary);

                chatRepo.createChat(chat,
                                Arrays.asList(userMsg, assistantMsg))
                        .whenComplete((id, err) -> {
                            if (err == null) {
                                Log.d(TAG, "Chat salvata con id=" + id);
                                result.postValue(new Result.Success<>(summary));
                            } else {
                                Log.e(TAG, "Errore Firestore", err);
                                result.postValue(new Result.Error(err.getMessage()));
                            }
                        });

            } catch (Exception e) {
                Log.e(TAG, "Errore generico", e);// stack-trace completo
                result.postValue(new Result.Error(e.getMessage()));
            }
        });
    }

}
