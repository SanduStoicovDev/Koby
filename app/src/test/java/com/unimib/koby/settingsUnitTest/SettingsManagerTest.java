package com.unimib.koby.settingsUnitTest;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import com.unimib.koby.ui.settings.SettingsManager;
import static com.google.common.truth.Truth.assertThat;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 34, manifest=Config.NONE)
public class SettingsManagerTest {

    private final Context ctx = ApplicationProvider.getApplicationContext();
    private final SettingsManager mgr = SettingsManager.getInstance(ctx);

    @After
    public void tearDown() {
        // reset so tests remain isolated
        mgr.setDarkTheme(false);
        mgr.setEnglish(false);
    }

    @Test
    public void singleton_returnsSameInstance() {
        SettingsManager mgr2 = SettingsManager.getInstance(ctx);
        assertThat(mgr).isSameInstanceAs(mgr2);
    }

    @Test
    public void darkTheme_getSet_persists() {
        mgr.setDarkTheme(false);
        assertThat(mgr.isDarkTheme()).isFalse();

        mgr.setDarkTheme(true);
        assertThat(mgr.isDarkTheme()).isTrue();
    }

    @Test
    public void english_getSet_persists() {
        mgr.setEnglish(false);
        assertThat(mgr.isEnglish()).isFalse();

        mgr.setEnglish(true);
        assertThat(mgr.isEnglish()).isTrue();
    }
}