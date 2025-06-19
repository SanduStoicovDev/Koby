package com.unimib.koby.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.unimib.koby.R;
import com.unimib.koby.adapter.ChatAdapter;
import com.unimib.koby.data.repository.chat.ChatRepository;
import com.unimib.koby.databinding.FragmentDashboardBinding;
import com.unimib.koby.model.Chat;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private ChatAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        ChatRepository repo = new ChatRepository();
        Query q = repo.allChats().orderBy("createdAt", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Chat> opts = new FirestoreRecyclerOptions.Builder<Chat>()
                .setQuery(q, Chat.class)
                .setLifecycleOwner(this)
                .build();

        adapter = new ChatAdapter(opts, (chat, id) -> {
            Bundle args = new Bundle();
            args.putString("chatId", id);
            args.putString("chatTitle", chat.getTitle());
            Navigation.findNavController(view)
                    .navigate(R.id.action_dashboard_to_chatDetail, args);
        });

        binding.recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recycler.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
