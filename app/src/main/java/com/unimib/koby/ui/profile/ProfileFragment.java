package com.unimib.koby.ui.profile;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.unimib.koby.R;
import com.unimib.koby.model.Result;
import com.unimib.koby.ui.login.UserViewModel;
import com.unimib.koby.ui.login.UserViewModelFactory;
import com.unimib.koby.util.ServiceLocator;

public class ProfileFragment extends Fragment {

    private ShapeableImageView avatar;
    private UserViewModel userVM;

    private ActivityResultLauncher<String> pickImage;
    private ActivityResultLauncher<String> permissionLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        super.onViewCreated(v, s);
        avatar = v.findViewById(R.id.imageAvatar);
        MaterialButton logoutBtn = v.findViewById(R.id.buttonLogout);
        MaterialSwitch langSwitch = v.findViewById(R.id.switchLanguage);
        MaterialSwitch themeSwitch = v.findViewById(R.id.switchTheme);

        // 1) registra pickImage prima, così la callback esiste già
        pickImage = registerForActivityResult(new ActivityResultContracts.GetContent(), this::handleImage);

        // 2) registra permissionLauncher che usa pickImage
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
            if (granted) pickImage.launch("image/*");
            else Toast.makeText(requireContext(), "Permesso negato", Toast.LENGTH_SHORT).show();
        });

        userVM = new ViewModelProvider(requireActivity(),
                new UserViewModelFactory(ServiceLocator.getInstance().getUserRepository(requireActivity().getApplication())))
                .get(UserViewModel.class);

        /* Placeholder: carica foto reale se disponibile */
        Glide.with(this)
                .load("https://placehold.co/128")
                .circleCrop()
                .into(avatar);

        avatar.setOnClickListener(vw -> {
            String perm = android.os.Build.VERSION.SDK_INT >= 33 ?
                    Manifest.permission.READ_MEDIA_IMAGES :
                    Manifest.permission.READ_EXTERNAL_STORAGE;
            if (ContextCompat.checkSelfPermission(requireContext(), perm) == PackageManager.PERMISSION_GRANTED) {
                pickImage.launch("image/*");
            } else permissionLauncher.launch(perm);
        });

        logoutBtn.setOnClickListener(vw -> userVM.logout().observe(getViewLifecycleOwner(), r -> {
            if (r.isSuccess()) requireActivity().finish();
            else Toast.makeText(requireContext(), ((Result.Error) r).getMessage(), Toast.LENGTH_LONG).show();
        }));

        // TODO: bind langSwitch / themeSwitch alle preferenze
    }

    private void handleImage(Uri uri) {
        if (uri == null) return;
        Glide.with(this).load(uri).circleCrop().into(avatar);
        // TODO: upload su Firebase Storage e aggiorna profilo
    }
}