package com.unimib.koby.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.unimib.koby.ui.MainActivity;
import com.unimib.koby.R;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText editEmail, editPassword;
    private MaterialButton buttonLogin;
    private TextView textRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textRegister = findViewById(R.id.textRegister);

        // Gestione login
        buttonLogin.setOnClickListener(v -> {
            String email = editEmail.getText().toString().trim();
            String password = editPassword.getText().toString().trim();

            if (email.isEmpty()) {
                editEmail.setError("Inserisci l'email");
                return;
            }

            if (password.isEmpty()) {
                editPassword.setError("Inserisci la password");
                return;
            }

            // Simulazione login
            if (email.equals("test@email.com") && password.equals("password123")) {
                // Login riuscito
                Toast.makeText(LoginActivity.this, "Login effettuato!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "Credenziali errate", Toast.LENGTH_SHORT).show();
            }
        });

        // Testo "Registrati" cliccabile
        makeRegisterClickable();
    }

    private void makeRegisterClickable() {
        String text = "Non hai un account? Registrati";
        SpannableString spannableString = new SpannableString(text);

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                // Vai a RegisterActivity
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        };

        spannableString.setSpan(clickableSpan, 23, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textRegister.setText(spannableString);
        textRegister.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
