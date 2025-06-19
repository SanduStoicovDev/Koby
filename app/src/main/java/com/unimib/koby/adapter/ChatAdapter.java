package com.unimib.koby.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.unimib.koby.R;
import com.unimib.koby.model.Chat;

/** Adapter che espone anche il click sulla card chat. */
public class ChatAdapter extends FirestoreRecyclerAdapter<Chat, ChatAdapter.VH> {

    /** Callback per il click. */
    public interface OnChatClickListener {
        /** @param chat  modello recuperato da Firestore
         *  @param chatId id del documento Firestore */
        void onChatClick(Chat chat, String chatId);
    }

    private final OnChatClickListener listener;

    public ChatAdapter(@NonNull FirestoreRecyclerOptions<Chat> options,
                       @NonNull OnChatClickListener listener) {
        super(options);
        this.listener = listener;
    }

    @Override
    protected void onBindViewHolder(@NonNull VH holder, int position, @NonNull Chat model) {
        holder.title.setText(model.getTitle());
        holder.subtitle.setText(model.getLastMessage());
        holder.itemView.setOnClickListener(v ->
                listener.onChatClick(model, getSnapshots().getSnapshot(position).getId()));
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat, parent, false);
        return new VH(v);
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView title, subtitle;
        VH(View v) {
            super(v);
            title = v.findViewById(R.id.tvTitle);
            subtitle = v.findViewById(R.id.tvSubtitle);
        }
    }
}
