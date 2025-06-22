package com.unimib.koby.ui.profile;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.unimib.koby.R;
import com.unimib.koby.data.repository.user.UserRepository;
import com.unimib.koby.ui.settings.SettingsViewModel;
import com.unimib.koby.ui.settings.SettingsViewModelFactory;
import com.unimib.koby.util.ServiceLocator;

public class ProfileFragment extends Fragment {

    private ShapeableImageView avatar;
    private ProfileViewModel profileVM;

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

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            ((TextView) v.findViewById(R.id.textName)).setText(currentUser.getDisplayName());
            ((TextView) v.findViewById(R.id.textEmail)).setText(currentUser.getEmail());
        }

        profileVM = new ViewModelProvider(
                this,
                new ProfileViewModelFactory((UserRepository) ServiceLocator.getInstance().getUserRepository(requireActivity().getApplication()))
        ).get(ProfileViewModel.class);

        profileVM.getPhotoUrl().observe(getViewLifecycleOwner(), uri ->
                Glide.with(this)
                        .load(uri)
                        .placeholder(R.drawable.ic_profile_placeholder)
                        .circleCrop()
                        .into(avatar)
        );

        // Switch lingua / tema
        SettingsViewModel settingsVM = new ViewModelProvider(
                this, new SettingsViewModelFactory(requireContext()))
                .get(SettingsViewModel.class);
        settingsVM.getEnglish().observe(getViewLifecycleOwner(), langSwitch::setChecked);
        settingsVM.getDark().observe(getViewLifecycleOwner(), themeSwitch::setChecked);
        langSwitch.setOnCheckedChangeListener((b, checked) -> settingsVM.toggleEnglish(checked));
        themeSwitch.setOnCheckedChangeListener((b, checked) -> settingsVM.toggleDark(checked));

        // Picker & permessi
        pickImage = registerForActivityResult(new ActivityResultContracts.GetContent(), this::handleImage);
        permissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
            if (granted) pickImage.launch("image/*");
            else Toast.makeText(requireContext(), "Permesso negato", Toast.LENGTH_SHORT).show();
        });

        avatar.setOnClickListener(vw -> {
            String perm = android.os.Build.VERSION.SDK_INT >= 33 ?
                    Manifest.permission.READ_MEDIA_IMAGES :
                    Manifest.permission.READ_EXTERNAL_STORAGE;
            if (ContextCompat.checkSelfPermission(requireContext(), perm) == PackageManager.PERMISSION_GRANTED) {
                pickImage.launch("image/*");
            } else permissionLauncher.launch(perm);
        });

        logoutBtn.setOnClickListener(vw -> {
            ServiceLocator.getInstance().getUserRepository(requireActivity().getApplication())
                    .logout()
                    .observe(getViewLifecycleOwner(), r -> requireActivity().finish());
        });
    }

    private void handleImage(Uri uri) {
        if (uri == null) return;
        Glide.with(this).load(uri).circleCrop().into(avatar); // preview immediata
        profileVM.uploadPhoto(uri);
    }
}
