package com.unimib.koby.ui;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.unimib.koby.R;
import com.unimib.koby.databinding.ActivityMainBinding;

import com.tom_roush.pdfbox.android.PDFBoxResourceLoader;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PDFBoxResourceLoader.init(getApplicationContext());

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Collegamento alla Toolbar personalizzata
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView navView = findViewById(R.id.bottom_nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_profile)
                .build();

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);
        NavController navController = navHostFragment.getNavController();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.bottomNavView, navController);

        //   2. Ascolta i cambi di destinazione
        navController.addOnDestinationChangedListener(
                (controller, destination, arguments) -> {

                    //   3. Se la destinazione è il tuo fragment di chat, nascondi
                    if (destination.getId() == R.id.fragment_new_chat) {
                        binding.bottomNavView.setVisibility(View.GONE);
                    } else {
                        binding.bottomNavView.setVisibility(View.VISIBLE);
                    }
                });
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);
        NavController navController = navHostFragment.getNavController();

        return NavigationUI.navigateUp(navController, new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_profile).build())
                || super.onSupportNavigateUp();
    }

}
