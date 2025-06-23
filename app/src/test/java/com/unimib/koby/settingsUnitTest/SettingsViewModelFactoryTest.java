package com.unimib.koby.settingsUnitTest;

import static com.google.common.truth.Truth.assertThat;

import static org.hamcrest.MatcherAssert.assertThat;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;

import com.unimib.koby.ui.settings.SettingsViewModel;
import com.unimib.koby.ui.settings.SettingsViewModelFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 34, manifest=Config.NONE)
public class SettingsViewModelFactoryTest {

    private final Context ctx = ApplicationProvider.getApplicationContext();
    private final SettingsViewModelFactory factory = new SettingsViewModelFactory(ctx);

    @Test
    public void create_returnsSettingsViewModel() {
        SettingsViewModel vm = factory.create(SettingsViewModel.class);
        assertThat(vm).isInstanceOf(SettingsViewModel.class);
    }
}