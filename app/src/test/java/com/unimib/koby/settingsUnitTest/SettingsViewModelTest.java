package com.unimib.koby.settingsUnitTest;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.unimib.koby.ui.settings.SettingsManager;
import com.unimib.koby.ui.settings.SettingsViewModel;

import org.junit.Rule;
import org.junit.Test;

public class SettingsViewModelTest {

    @Rule
    public final InstantTaskExecutorRule instantRule = new InstantTaskExecutorRule();

    private final SettingsManager mgr = mock(SettingsManager.class);

    @Test
    public void constructor_exposesInitialValues() {
        when(mgr.isDarkTheme()).thenReturn(true);
        when(mgr.isEnglish()).thenReturn(false);

        SettingsViewModel vm = new SettingsViewModel(mgr);

        // Observe forever to force LiveData to emit immediately
        Observer<Boolean> noop = b -> {};
        vm.getDark().observeForever(noop);
        vm.getEnglish().observeForever(noop);

        assertThat(Boolean.FALSE.equals(vm.getDark().getValue())).isTrue();
        assertThat(Boolean.TRUE.equals(vm.getEnglish().getValue())).isFalse();

        vm.getDark().removeObserver(noop);
        vm.getEnglish().removeObserver(noop);
    }

    @Test
    public void toggleDark_updatesManagerAndLiveData() {
        SettingsViewModel vm = new SettingsViewModel(mgr);
        Observer<Boolean> noop = b -> {};
        vm.getDark().observeForever(noop);

        vm.toggleDark(true);

        verify(mgr).setDarkTheme(true);
        assertThat(Boolean.TRUE.equals(vm.getDark().getValue())).isTrue();

        vm.getDark().removeObserver(noop);
    }

    @Test
    public void toggleEnglish_updatesManagerAndLiveData() {
        SettingsViewModel vm = new SettingsViewModel(mgr);
        Observer<Boolean> noop = b -> {};
        vm.getEnglish().observeForever(noop);

        vm.toggleEnglish(true);

        verify(mgr).setEnglish(true);
        assertThat(Boolean.TRUE.equals(vm.getEnglish().getValue())).isTrue();

        vm.getEnglish().removeObserver(noop);
    }
}