package com.unimib.koby.ui.home;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.unimib.koby.R;
import com.unimib.koby.adapter.ChatAdapter;
import com.unimib.koby.databinding.FragmentHomeBinding;
import com.unimib.koby.model.Chat;
import com.unimib.koby.data.repository.chat.ChatRepository;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private ChatAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        binding = FragmentHomeBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,@Nullable Bundle savedInstanceState){
        ChatRepository repo = new ChatRepository();
        Query q = repo.allChats().orderBy("createdAt", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Chat> opts = new FirestoreRecyclerOptions.Builder<Chat>()
                .setQuery(q, Chat.class)
                .setLifecycleOwner(this)
                .build();
        adapter = new ChatAdapter(opts);
        binding.recycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recycler.setAdapter(adapter);

        binding.fabNewChat.setOnClickListener(v->
                NavHostFragment.findNavController(this).navigate(R.id.action_home_to_newChat));
    }
}
