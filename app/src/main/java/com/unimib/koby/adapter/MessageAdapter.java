package com.unimib.koby.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.unimib.koby.R;
import com.unimib.koby.model.ChatMessage;

public class MessageAdapter extends ListAdapter<ChatMessage, MessageAdapter.VH> {

    public MessageAdapter() {
        super(new DiffUtil.ItemCallback<ChatMessage>() {
            @Override
            public boolean areItemsTheSame(@NonNull ChatMessage a, @NonNull ChatMessage b) {
                // Use Firestore id when available, otherwise timestamp fallback
                return (a.getId() != null && a.getId().equals(b.getId())) ||
                        (a.getId() == null && a.getTimestamp() == b.getTimestamp());
            }
            @Override
            public boolean areContentsTheSame(@NonNull ChatMessage a, @NonNull ChatMessage b) {
                return a.equals(b);
            }
        });
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView text;
        VH(@NonNull View v) { super(v); text = v.findViewById(R.id.messageText); }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = (viewType == 0) ? R.layout.item_user : R.layout.item_assistant;
        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        holder.text.setText(getItem(position).getContent());
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getRole().equals("user") ? 0 : 1;
    }
}