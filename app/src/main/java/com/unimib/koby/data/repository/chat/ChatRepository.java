package com.unimib.koby.data.repository.chat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.unimib.koby.model.Chat;
import com.unimib.koby.model.ChatMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ChatRepository {
    private final CollectionReference chatColl;

    public ChatRepository() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        chatColl = FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("chats");
    }

    public CompletableFuture<String> createChat(Chat chat, List<ChatMessage> firstMessages) {
        CompletableFuture<String> f = new CompletableFuture<>();
        chatColl.add(chat.toMap()).addOnSuccessListener(doc -> {
            String chatId = doc.getId();
            for (ChatMessage m : firstMessages) {
                doc.collection("messages").add(m);
            }
            f.complete(chatId);
        }).addOnFailureListener(f::completeExceptionally);
        return f;
    }

    public CollectionReference allChats() { return chatColl; }

    /** Stream reattivo della cronologia ordinata di una chat. */
    public LiveData<List<ChatMessage>> messagesOf(String chatId) {
        MutableLiveData<List<ChatMessage>> live = new MutableLiveData<>();
        chatColl.document(chatId)
                .collection("messages")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((snap, e) -> {
                    if (e != null || snap == null) return;
                    List<ChatMessage> list = new ArrayList<>();
                    for (DocumentSnapshot ds : snap.getDocuments()) {
                        ChatMessage m = ds.toObject(ChatMessage.class);
                        if (m != null) m.setId(ds.getId());
                        list.add(m);
                    }
                    live.setValue(list);
                });
        return live;
    }
}
