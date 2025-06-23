package com.unimib.koby.ui.settings;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SettingsViewModel extends ViewModel {

    private final SettingsManager mgr;
    private final MutableLiveData<Boolean> darkLive = new MutableLiveData<>();
    private final MutableLiveData<Boolean> enLive   = new MutableLiveData<>();

    public SettingsViewModel(SettingsManager mgr) {
        this.mgr = mgr;
        darkLive.setValue(false);
        enLive.setValue(false);
    }

    public LiveData<Boolean> getDark() { return darkLive; }
    public LiveData<Boolean> getEnglish() { return enLive; }

    public void toggleDark(boolean dark) {
        mgr.setDarkTheme(dark);
        darkLive.setValue(dark);
        AppCompatDelegate.setDefaultNightMode(dark ?
                AppCompatDelegate.MODE_NIGHT_YES : //Non funziona piu correttamente è in BETA
                AppCompatDelegate.MODE_NIGHT_NO);
    }

    public void toggleEnglish(boolean en) {
        mgr.setEnglish(en);
        enLive.setValue(en);
        // AppCompatDelegate (AndroidX 1.6+) applica locale app‑wide senza ricreare activity
        LocaleListCompat locales = LocaleListCompat.forLanguageTags(en ? "en" : "it");
        AppCompatDelegate.setApplicationLocales(locales);
    }
}