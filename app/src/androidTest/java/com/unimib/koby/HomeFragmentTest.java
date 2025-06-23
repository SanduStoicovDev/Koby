package com.unimib.koby;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
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
import com.unimib.koby.ui.home.HomeFragment;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * UI-test strumentati per HomeFragment.
 *
 * Copre:
 * 1) Il fab che deve portare al NewChatFragment
 * 2) La presenza dei due TextView animati (welcome e sottotitolo)
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class HomeFragmentTest {

    private TestNavHostController navController;

    @Before
    public void setUp() {
        navController = new TestNavHostController(
                ApplicationProvider.getApplicationContext());

        // Qualunque operazione che modifichi Lifecycle **va** sul main thread
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
            navController.setGraph(R.navigation.mobile_navigation);
            navController.setCurrentDestination(R.id.navigation_home);
        });
    }

    /* ---------- TEST 1: clic FAB → naviga a NewChatFragment ---------- */
    @Test
    public void clickFab_navigatesToNewChatFragment() {

        FragmentScenario<HomeFragment> scenario =
                FragmentScenario.launchInContainer(HomeFragment.class, new Bundle(),
                        R.style.AppTheme);

        scenario.onFragment(fragment ->
                Navigation.setViewNavController(fragment.requireView(), navController));

        // Clic sul FloatingActionButton
        onView(withId(R.id.fabNewChat)).perform(click());

        // Dovremmo trovarci sul nuovo destination
        assertThat(navController.getCurrentDestination().getId(),
                is(R.id.fragment_new_chat));
    }

    /* ---------- TEST 2: TextView visibili al caricamento ---------- */
    @Test
    public void welcomeAndSubtitle_areVisibleOnLaunch() {

        FragmentScenario<HomeFragment> scenario =
                FragmentScenario.launchInContainer(HomeFragment.class, new Bundle(),
                        R.style.AppTheme);

        scenario.onFragment(fragment ->
                Navigation.setViewNavController(fragment.requireView(), navController));

        // Verifica che i due TextView siano presenti nella gerarchia
        onView(withId(R.id.tvWelcome)).check(matches(isDisplayed()));
        onView(withId(R.id.tvSubtitle)).check(matches(isDisplayed()));
    }
}