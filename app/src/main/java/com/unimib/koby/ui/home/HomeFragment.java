package com.unimib.koby.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.unimib.koby.R;
import com.unimib.koby.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {

        // → Navigazione esistente verso “Nuova chat”
        binding.fabNewChat.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_home_to_newChat));

        // --- Animazioni ----------------------------------------------------
        // Titolo: scende dall’alto con fade-in
        binding.tvWelcome.setAlpha(0f);
        binding.tvWelcome.setScaleX(0.8f);
        binding.tvWelcome.setScaleY(0.8f);
        binding.tvWelcome.animate()
                .alpha(1f)
                .scaleX(1f).scaleY(1f)
                .translationY(0)
                .setDuration(800)
                .setInterpolator(new OvershootInterpolator())
                .start();


        // Sottotitolo: fade-in ritardato
        binding.tvSubtitle.setAlpha(0f);
        binding.tvSubtitle.animate()
                .alpha(1f)
                .setDuration(700)
                .setStartDelay(500)
                .start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
