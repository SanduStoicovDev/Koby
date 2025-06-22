package com.unimib.koby.ui.newchat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.unimib.koby.adapter.MessageAdapter;
import com.unimib.koby.databinding.FragmentNewChatBinding;
import com.unimib.koby.model.Result;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Fragment UI per la chat in stile ChatGPT.
 */
public class NewChatFragment extends Fragment {

    private FragmentNewChatBinding binding;
    private NewChatViewModel vm;
    private MessageAdapter adapter;

    private ActivityResultLauncher<Intent> pdfPicker;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentNewChatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vm = new ViewModelProvider(this).get(NewChatViewModel.class);

        final View root          = binding.getRoot();                 // ConstraintLayout
        final View composer      = binding.messageBar;     // la card con EditText
        final RecyclerView listTastiera  = binding.rvMessages;                // messaggi

        // ---------------- RecyclerView ----------------
        adapter = new MessageAdapter();
        LinearLayoutManager lm = new LinearLayoutManager(requireContext());
        lm.setStackFromEnd(true);
        binding.rvMessages.setLayoutManager(lm);
        binding.rvMessages.setAdapter(adapter);

        vm.getMessages().observe(getViewLifecycleOwner(), list -> {
            adapter.submitList(new ArrayList<>(list), () -> {
                int last = adapter.getItemCount() - 1;
                if (last >= 0) binding.rvMessages.smoothScrollToPosition(last);
            });
        });

        // ---------------- Result handling ----------------
        vm.getResult().observe(getViewLifecycleOwner(), r -> {
            if (r instanceof Result.Error) {
                String msg = ((Result.Error) r).getMessage();
                Snackbar.make(binding.getRoot(), msg, Snackbar.LENGTH_LONG).show();
            }
        });

        // ---------------- PDF picker ----------------
        pdfPicker = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), res -> {
            if (res.getResultCode() == Activity.RESULT_OK && res.getData() != null) {
                try {
                    InputStream in = requireContext().getContentResolver().openInputStream(res.getData().getData());
                    vm.setPendingPdf(in);
                    Toast.makeText(requireContext(), "PDF allegato. Premi Invia!", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Snackbar.make(binding.getRoot(), "Impossibile leggere il PDF", Snackbar.LENGTH_LONG).show();
                }
            }
        });

        //Gestione Tastiera
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {

            int imeBottom = insets.getInsets(WindowInsetsCompat.Type.ime()).bottom;

            composer.setTranslationY(-imeBottom);

            listTastiera.setPadding(
                    listTastiera.getPaddingLeft(),
                    listTastiera.getPaddingTop(),
                    listTastiera.getPaddingRight(),
                    imeBottom + composer.getHeight()
            );

            return insets;
        });

        binding.btnPickPdf.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");
            pdfPicker.launch(Intent.createChooser(intent, "Seleziona PDF"));
        });

        // ---------------- Send button ----------------
        binding.btnSend.setOnClickListener(v -> {
            String text = Objects.requireNonNull(binding.editMessage.getText()).toString().trim();
            if (text.isEmpty()) return;
            binding.editMessage.setText("");
            vm.send(text);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}