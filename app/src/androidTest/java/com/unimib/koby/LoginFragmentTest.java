package com.unimib.koby;

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
import com.unimib.koby.ui.login.LoginFragment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * UI-test strumentati per LoginFragment che girano sul main thread
 * quando interagiscono con NavController / Lifecycle.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginFragmentTest {

    private TestNavHostController navController;

    @Before
    public void setUp() {
        navController = new TestNavHostController(
                ApplicationProvider.getApplicationContext());

        // Tutto ciò che tocca Lifecycle va fatto sul main thread
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
            navController.setGraph(R.navigation.login_nav_graph);
            navController.setCurrentDestination(R.id.loginFragment);
        });
    }

    /* ---------- TEST 1: link "Registrati" ---------- */
    @Test
    public void clickRegister_navigatesToRegisterFragment() {

        FragmentScenario<LoginFragment> scenario =
                FragmentScenario.launchInContainer(LoginFragment.class, new Bundle(),
                        R.style.AppTheme);

        scenario.onFragment(fragment ->
                Navigation.setViewNavController(fragment.requireView(), navController));

        onView(withId(R.id.textRegister)).perform(click());

        assertThat(navController.getCurrentDestination().getId(),
                is(R.id.registrationFragment));
    }

    /* ---------- TEST 2: validazione campi vuoti ---------- */
    @Test
    public void emptyFields_showsValidationErrors() {

        FragmentScenario<LoginFragment> scenario =
                FragmentScenario.launchInContainer(LoginFragment.class, null,
                        R.style.AppTheme);

        scenario.onFragment(fragment ->
                Navigation.setViewNavController(fragment.requireView(), navController));

        onView(withId(R.id.buttonLogin)).perform(click());

        onView(withId(R.id.emailLayout))
                .check(matches(hasDescendant(withText(R.string.error_email))));
        onView(withId(R.id.passwordLayout))
                .check(matches(hasDescendant(withText(R.string.error_password))));
    }

    /* ---------- TEST 3: login riuscito (scheletro) ---------- */
    @Test
    public void successfulLogin_navigatesToMainActivity() {
        // TODO: inietta un LoginViewModel fake che emetta Result.Success

        FragmentScenario<LoginFragment> scenario =
                FragmentScenario.launchInContainer(LoginFragment.class, null,
                        R.style.AppTheme);

        scenario.onFragment(fragment ->
                Navigation.setViewNavController(fragment.requireView(), navController));

        onView(withId(R.id.editEmail))
                .perform(typeText("SS03@example.com"), closeSoftKeyboard());
        onView(withId(R.id.editPassword))
                .perform(typeText("Ciao1234!"), closeSoftKeyboard());

        onView(withId(R.id.buttonLogin)).perform(click());

        assertThat(navController.getCurrentDestination().getId(),
                is(R.id.mainActivity));
    }
}
