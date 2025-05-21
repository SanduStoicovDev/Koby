package com.unimib.koby.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.unimib.koby.R;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText editName, editEmail, editPassword, editConfirmPassword;
    private MaterialButton buttonRegister, buttonGoToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        editConfirmPassword = findViewById(R.id.editConfirmPassword);
        buttonRegister = findViewById(R.id.buttonRegister);
        buttonGoToLogin = findViewById(R.id.buttonGoToLogin);

        buttonRegister.setOnClickListener(v -> {
            String name = editName.getText().toString().trim();
            String email = editEmail.getText().toString().trim();
            String password = editPassword.getText().toString();
            String confirmPassword = editConfirmPassword.getText().toString();

            if (TextUtils.isEmpty(name)) {
                editName.setError("Inserisci il tuo nome");
                return;
            }

            if (TextUtils.isEmpty(email)) {
                editEmail.setError("Inserisci l'email");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                editPassword.setError("Inserisci la password");
                return;
            }

            if (!password.equals(confirmPassword)) {
                editConfirmPassword.setError("Le password non coincidono");
                return;
            }

            // Simulazione registrazione
            Toast.makeText(RegisterActivity.this, "Registrazione completata!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });

        buttonGoToLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });
    }
}
