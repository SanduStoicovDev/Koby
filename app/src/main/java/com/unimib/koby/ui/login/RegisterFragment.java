package com.unimib.koby.ui.login;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.unimib.koby.R;
import com.unimib.koby.model.Result;
import com.unimib.koby.util.ServiceLocator;

/**
 * Schermata di registrazione (v2) – sincronizzata con gli ID presenti
 * in fragment_register.xml.
 */
public class RegisterFragment extends Fragment {

    private TextInputLayout nameLayout, emailLayout, pwdLayout, confirmPwdLayout;
    private TextInputEditText nameEdit, emailEdit, pwdEdit, confirmPwdEdit;
    private UserViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- View binding --------------------------------------------------
        nameLayout       = view.findViewById(R.id.nameLayout);
        emailLayout      = view.findViewById(R.id.emailLayout);
        pwdLayout        = view.findViewById(R.id.passwordLayout);
        confirmPwdLayout = view.findViewById(R.id.confirmPasswordLayout);

        nameEdit         = view.findViewById(R.id.editName);
        emailEdit        = view.findViewById(R.id.editEmail);
        pwdEdit          = view.findViewById(R.id.editPassword);
        confirmPwdEdit   = view.findViewById(R.id.editConfirmPassword);

        View registerBtn = view.findViewById(R.id.buttonRegister);
        View loginLink   = view.findViewById(R.id.buttonGoToLogin);

        // --- ViewModel -----------------------------------------------------
        viewModel = new ViewModelProvider(
                requireActivity(),
                new UserViewModelFactory(ServiceLocator.getInstance()
                        .getUserRepository(requireActivity().getApplication()))
        ).get(UserViewModel.class);

        viewModel.getLoading().observe(getViewLifecycleOwner(), loading ->
                registerBtn.setEnabled(!Boolean.TRUE.equals(loading)));

        // --- Click listeners ----------------------------------------------
        registerBtn.setOnClickListener(v -> attemptRegister());
        loginLink.setOnClickListener(v -> Navigation.findNavController(v).navigateUp());
    }

    private void attemptRegister() {
        String name     = getString(nameEdit);
        String email    = getString(emailEdit);
        String password = getString(pwdEdit);
        String confirm  = getString(confirmPwdEdit);

        boolean valid = true;
        if (TextUtils.isEmpty(name)) {
            nameLayout.setError(getString(R.string.error_name));
            valid = false;
        } else nameLayout.setError(null);

        if (TextUtils.isEmpty(email)) {
            emailLayout.setError(getString(R.string.error_email));
            valid = false;
        } else emailLayout.setError(null);

        if (TextUtils.isEmpty(password) || password.length() < 8) {
            pwdLayout.setError(getString(R.string.error_password));
            valid = false;
        } else pwdLayout.setError(null);

        if (!password.equals(confirm)) {
            confirmPwdLayout.setError(getString(R.string.error_password_mismatch));
            valid = false;
        } else confirmPwdLayout.setError(null);

        if (!valid) return;

        viewModel.register(name, email, password).observe(getViewLifecycleOwner(), result -> {
            if (result.isSuccess()) {
                Toast.makeText(requireContext(), R.string.registration_success, Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).navigateUp();
            } else {
                Toast.makeText(requireContext(), ((Result.Error) result).getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private String getString(TextInputEditText edit) {
        return edit.getText() != null ? edit.getText().toString().trim() : "";
    }
}
