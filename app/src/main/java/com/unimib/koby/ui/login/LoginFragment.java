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
import androidx.fragment.app.Fragment;


/** Concrete screen that gathers user credentials and delegates to the ViewModel. */
public class LoginFragment extends Fragment {

    public LoginFragment() {
        // Required empty public constructor
    }

    private TextInputLayout emailLayout, passwordLayout;
    private TextInputEditText emailEdit, passwordEdit;
    private UserViewModel viewModel;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- View binding (kept manual for brevity) -----------------------------------------
        emailLayout = view.findViewById(R.id.emailLayout);
        passwordLayout = view.findViewById(R.id.passwordLayout);
        emailEdit   = view.findViewById(R.id.editEmail);
        passwordEdit= view.findViewById(R.id.editPassword);
        View loginButton = view.findViewById(R.id.buttonLogin);
        View registerLink = view.findViewById(R.id.textRegister);

        // --- ViewModel ----------------------------------------------------------------------
        viewModel = new ViewModelProvider(
                requireActivity(),
                new UserViewModelFactory(ServiceLocator.getInstance().getUserRepository(requireActivity().getApplication()))
        ).get(UserViewModel.class);

        // Observe loading state (could be used to show a ProgressBar)
        viewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            loginButton.setEnabled(!Boolean.TRUE.equals(isLoading));
        });

        // --- Click listeners ----------------------------------------------------------------
        loginButton.setOnClickListener(v -> attemptLogin());
        registerLink.setOnClickListener(v -> Navigation.findNavController(v)
                .navigate(R.id.action_loginFragment_to_registerFragment));
    }

    private void attemptLogin() {
        String email = emailEdit.getText() != null ? emailEdit.getText().toString().trim() : "";
        String password = passwordEdit.getText() != null ? passwordEdit.getText().toString() : "";

        boolean valid = true;
        if (TextUtils.isEmpty(email)) {
            emailLayout.setError(getString(R.string.error_email));
            valid = false;
        } else {
            emailLayout.setError(null);
        }
        if (TextUtils.isEmpty(password) || password.length() < 8) {
            passwordLayout.setError(getString(R.string.error_password));
            valid = false;
        } else {
            passwordLayout.setError(null);
        }
        if (!valid) return;

        viewModel.login(email, password).observe(getViewLifecycleOwner(), result -> {
            if (result.isSuccess()) {
                Toast.makeText(requireContext(), R.string.login_success, Toast.LENGTH_SHORT).show();
                // Navigate to your Home / Main flow here
                Navigation.findNavController(requireView()).navigate(R.id.action_loginFragment_to_mainActivity);
                requireActivity().finish();
            } else {
                Toast.makeText(requireContext(), ((Result.Error) result).getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
