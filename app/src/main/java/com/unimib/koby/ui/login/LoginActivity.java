package com.unimib.koby.ui.login;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.unimib.koby.R;

/** Hosts the navigation graph defined in activity_login.xml – no UI logic here. */
public class LoginActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
}
