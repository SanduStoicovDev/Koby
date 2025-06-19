package com.unimib.koby.ui.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.unimib.koby.adapter.MessageAdapter;
import com.unimib.koby.databinding.FragmentChatDetailBinding;

public class ChatDetailFragment extends Fragment {

    private FragmentChatDetailBinding binding;
    private MessageAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChatDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        String chatId   = requireArguments().getString("chatId");
        String chatName = requireArguments().getString("chatTitle");

        requireActivity().setTitle(chatName); // mostra il titolo in Toolbar

        ChatDetailViewModel vm = new ViewModelProvider(
                this, new ChatDetailViewModel.Factory(chatId))
                .get(ChatDetailViewModel.class);

        adapter = new MessageAdapter();
        binding.recyclerMessages.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerMessages.setAdapter(adapter);

        vm.getMessages().observe(getViewLifecycleOwner(), adapter::submitList);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
