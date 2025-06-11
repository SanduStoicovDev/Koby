package com.unimib.koby.data.repository.chat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.unimib.koby.model.Chat;
import com.unimib.koby.model.ChatMessage;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ChatRepository {
    private final CollectionReference chatColl;

    public ChatRepository(){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        chatColl = FirebaseFirestore.getInstance().collection("users").document(uid).collection("chats");
    }

    public CompletableFuture<String> createChat(Chat chat, List<ChatMessage> firstMessages){
        CompletableFuture<String> f = new CompletableFuture<>();
        chatColl.add(chat.toMap()).addOnSuccessListener(doc->{
            String chatId = doc.getId();
            for(ChatMessage m:firstMessages){
                doc.collection("messages").add(m);
            }
            f.complete(chatId);
        }).addOnFailureListener(f::completeExceptionally);
        return f;
    }

    public CollectionReference allChats(){ return chatColl; }
}