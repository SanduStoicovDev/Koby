package com.unimib.koby.ui.chat;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.unimib.koby.data.repository.chat.ChatRepository;
import com.unimib.koby.model.ChatMessage;

import java.util.List;

public class ChatDetailViewModel extends ViewModel {
    private final LiveData<List<ChatMessage>> messages;

    private ChatDetailViewModel(@NonNull String chatId) {
        messages = new ChatRepository().messagesOf(chatId);
    }

    public LiveData<List<ChatMessage>> getMessages() { return messages; }

    /** Factory per passare il parametro obbligatorio chatId. */
    public static class Factory implements ViewModelProvider.Factory {
        private final String chatId;
        public Factory(String chatId) { this.chatId = chatId; }
        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new ChatDetailViewModel(chatId);
        }
    }
}
