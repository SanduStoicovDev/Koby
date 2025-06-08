package com.unimib.koby.ui.spacestudy;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.unimib.koby.R;
import com.unimib.koby.model.Result;
import com.unimib.koby.util.ServiceLocator;

/**
 * Fragment per selezionare e caricare un PDF.
 */
public class UploadSpaceFragment extends Fragment {

    private static final String MIME_PDF = "application/pdf";

    private TextInputEditText titleEdit;
    private MaterialButton pickBtn, uploadBtn;
    private ProgressBar progressBar;
    private Uri selectedPdf;

    private SpaceStudyViewModel viewModel;

    private final ActivityResultLauncher<Intent> pickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                    selectedPdf = result.getData().getData();
                    if (selectedPdf != null) {
                        String name = DocumentsContract.getDocumentId(selectedPdf);
                        pickBtn.setText(name != null ? name : "PDF selezionato");
                    }
                }
            });

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_upload_space, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        titleEdit = view.findViewById(R.id.editTitle);
        pickBtn   = view.findViewById(R.id.buttonPick);
        uploadBtn = view.findViewById(R.id.buttonUpload);
        progressBar = view.findViewById(R.id.progressUpload);

        viewModel = new ViewModelProvider(
                requireActivity(),
                new SpaceStudyViewModelFactory(ServiceLocator.getInstance().getSpaceStudyRepository())
        ).get(SpaceStudyViewModel.class);

        pickBtn.setOnClickListener(v -> openFilePicker());
        uploadBtn.setOnClickListener(v -> uploadPdf());

        viewModel.getLoading().observe(getViewLifecycleOwner(), loading -> {
            progressBar.setVisibility(Boolean.TRUE.equals(loading) ? View.VISIBLE : View.GONE);
            uploadBtn.setEnabled(!Boolean.TRUE.equals(loading));
        });
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType(MIME_PDF);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        pickerLauncher.launch(intent);
    }

    private void uploadPdf() {
        String title = titleEdit.getText() != null ? titleEdit.getText().toString().trim() : "";
        if (selectedPdf == null) {
            Toast.makeText(requireContext(), "Seleziona un PDF", Toast.LENGTH_SHORT).show();
            return;
        }
        if (title.isEmpty()) {
            titleEdit.setError("Inserisci un titolo");
            return;
        }
        viewModel.upload(selectedPdf, title).observe(getViewLifecycleOwner(), res -> {
            if (res.isSuccess()) {
                Toast.makeText(requireContext(), "Caricato!", Toast.LENGTH_SHORT).show();
                requireActivity().onBackPressed();
            } else {
                Toast.makeText(requireContext(), ((Result.Error) res).getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}