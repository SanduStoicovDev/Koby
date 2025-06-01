package com.unimib.koby.ui.login;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.unimib.koby.R;

/**
 * Activity di registrazione: ospita soltanto il layout activity_register che
 * include <fragment ... RegisterFragment>. Nessuna logica UI qui per rispettare MVVM.
 */
public class RegisterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }
}
