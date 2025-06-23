package com.unimib.koby.ui.login;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import android.os.Bundle;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.navigation.Navigation;
import androidx.navigation.testing.TestNavHostController;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.unimib.koby.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * UI-test strumentati per RegisterFragment.
 * – Copre: link “Accedi”, validazione campi vuoti, scheletro registrazione ok.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class RegisterFragmentTest {

    private TestNavHostController navController;

    @Before
    public void setUp() {
        navController = new TestNavHostController(
                ApplicationProvider.getApplicationContext());

        // Tutto ciò che tocca LifecycleRegistry DEVE stare sul main thread.
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
            navController.setGraph(R.navigation.login_nav_graph);

            // Simuliamo il percorso Login → Register quindi:
            navController.setCurrentDestination(R.id.loginFragment);     // pagina precedente
            navController.navigate(R.id.registrationFragment);               // pagina in test
        });
    }

    /* ---------- TEST 1: link “Hai già un account? Accedi” ---------- */
    @Test
    public void clickLoginLink_navigatesBackToLoginFragment() {

        FragmentScenario<RegisterFragment> scenario =
                FragmentScenario.launchInContainer(RegisterFragment.class, new Bundle(),
                        R.style.AppTheme);

        scenario.onFragment(fragment ->
                Navigation.setViewNavController(fragment.requireView(), navController));

        onView(withId(R.id.textRegister)).perform(click());

        assertThat(navController.getCurrentDestination().getId(),
                is(R.id.loginFragment));
    }

    /* ---------- TEST 2: validazione campi vuoti ---------- */
    @Test
    public void emptyFields_showsValidationErrors() {

        FragmentScenario<RegisterFragment> scenario =
                FragmentScenario.launchInContainer(RegisterFragment.class, new Bundle(),
                        R.style.AppTheme);

        scenario.onFragment(fragment ->
                Navigation.setViewNavController(fragment.requireView(), navController));

        onView(withId(R.id.buttonRegister)).perform(click());

        onView(withId(R.id.nameLayout))
                .check(matches(hasDescendant(withText(R.string.error_name))));
        onView(withId(R.id.emailLayout))
                .check(matches(hasDescendant(withText(R.string.error_email))));
        onView(withId(R.id.passwordLayout))
                .check(matches(hasDescendant(withText(R.string.error_password))));
    }

    /* ---------- TEST 3: registrazione riuscita (scheletro) ---------- */
    @Test
    public void successfulRegister_navigatesBackToLoginFragment() {
        // TODO: inietta un UserViewModel fake che restituisca Result.Success

        FragmentScenario<RegisterFragment> scenario =
                FragmentScenario.launchInContainer(RegisterFragment.class, new Bundle(),
                        R.style.AppTheme);

        scenario.onFragment(fragment ->
                Navigation.setViewNavController(fragment.requireView(), navController));

        // Compila i campi
        onView(withId(R.id.editName))
                .perform(typeText("Sandu Test"), closeSoftKeyboard());
        onView(withId(R.id.editEmail))
                .perform(typeText("SS02@example.com"), closeSoftKeyboard());
        onView(withId(R.id.editPassword))
                .perform(typeText("Ciao1234!"), closeSoftKeyboard());
        onView(withId(R.id.editConfirmPassword))
                .perform(typeText("Ciao1234!"), closeSoftKeyboard());

        // Clicca “Registrati”
        onView(withId(R.id.buttonRegister)).perform(click());

        // Quando il fake ViewModel emette Success, il fragment fa navigateUp()
        assertThat(navController.getCurrentDestination().getId(),
                is(R.id.loginFragment));
    }
}
