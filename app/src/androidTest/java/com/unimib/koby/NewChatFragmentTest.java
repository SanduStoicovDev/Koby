package com.unimib.koby;

import static android.view.KeyEvent.KEYCODE_BACK;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThrows;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentFactory;
import androidx.fragment.app.testing.FragmentScenario;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.viewmodel.CreationExtras;
import androidx.navigation.Navigation;
import androidx.navigation.testing.TestNavHostController;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.NoActivityResumedException;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.platform.app.InstrumentationRegistry;

import com.unimib.koby.model.ChatMessage;
import com.unimib.koby.model.Result;
import com.unimib.koby.ui.newchat.NewChatFragment;
import com.unimib.koby.ui.newchat.NewChatViewModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

/**
 * UI-test per NewChatFragment che evita Firebase e LiveData null.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class NewChatFragmentTest {

    /* ---------- Stub pubblico/statico ---------- */
    public static class TestNewChatFragment extends NewChatFragment {
        private static ViewModelProvider.Factory sFactory;
        static void injectFactory(ViewModelProvider.Factory f) { sFactory = f; }

        @NonNull @Override
        public ViewModelProvider.Factory getDefaultViewModelProviderFactory() {
            return sFactory != null ? sFactory : super.getDefaultViewModelProviderFactory();
        }
    }

    /* ---------- Factory che genera lo stub e conserva il VM mock ---------- */
    private static class NewChatTestFactory extends FragmentFactory {

        final MutableLiveData<List<ChatMessage>> messagesLd = new MutableLiveData<>(new ArrayList<>());
        final MutableLiveData<Boolean>           loadingLd  = new MutableLiveData<>(false);
        final MutableLiveData<Result>            resultLd   = new MutableLiveData<>();
        final MutableLiveData<Boolean>           sendEnabledLd   = new MutableLiveData<Boolean>();

        private NewChatViewModel vmMock;
        NewChatViewModel getVm() { return vmMock; }

        @NonNull @Override
        public Fragment instantiate(@NonNull ClassLoader cl, @NonNull String name) {
            if (name.equals(NewChatFragment.class.getName())) {

                vmMock = Mockito.mock(NewChatViewModel.class);

                Mockito.when(vmMock.getMessages()).thenReturn(messagesLd);
                Mockito.when(vmMock.getLoading()) .thenReturn(loadingLd);
                Mockito.when(vmMock.getResult())  .thenReturn(resultLd);
                Mockito.doNothing().when(vmMock).send(Mockito.anyString());

                ViewModelProvider.Factory factory = new ViewModelProvider.Factory() {
                    @NonNull @SuppressWarnings("unchecked")
                    @Override
                    public <T extends ViewModel> T create(@NonNull Class<T> c) {
                        if (c.isAssignableFrom(NewChatViewModel.class)) return (T) vmMock;
                        throw new IllegalArgumentException(c.getName());
                    }
                    @NonNull @SuppressWarnings("unchecked")
                    @Override
                    public <T extends ViewModel> T create(@NonNull Class<T> c,
                                                          @NonNull CreationExtras e) {
                        return create(c);
                    }
                };

                TestNewChatFragment.injectFactory(factory);
                return new TestNewChatFragment();
            }
            return super.instantiate(cl, name);
        }
    }

    /* ---------- oggetti di supporto ---------- */
    private NewChatTestFactory factory;
    private TestNavHostController nav;

    @Before
    public void setUp() {
        nav = new TestNavHostController(ApplicationProvider.getApplicationContext());
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
            nav.setGraph(R.navigation.mobile_navigation);
            nav.setCurrentDestination(R.id.navigation_home);   // start reale
            nav.navigate(R.id.fragment_new_chat);              // pagina in test
        });
        factory = new NewChatTestFactory();
    }

    /* ---------- TEST 1 ---------- */
    @Test
    public void sendButton_enablesOnlyWhenTextIsTyped() {

        // 1. avvia il fragment in test
        FragmentScenario.launchInContainer(
                NewChatFragment.class, /* args */ null,
                R.style.AppTheme, factory        // factory già inizializzata nel @Before
        ).onFragment(f -> Navigation.setViewNavController(f.requireView(), nav));

        // 2. parte disabilitato
        //onView(withId(R.id.btnSend)).check(matches(not(isEnabled())));

        // 3. digita un testo
        String prompt = "Hello GPT!";
        onView(withId(R.id.editMessage))
                .perform(typeText(prompt), closeSoftKeyboard());

        // 4. il ViewModel, di solito, al text-changed imposta sendEnabled=true
        //    Nel test lo simuliamo noi -> postiamo true sul LiveData **sul main thread**
        InstrumentationRegistry.getInstrumentation().runOnMainSync(
                () -> factory.sendEnabledLd.setValue(true)
        );

        // 5. adesso il bottone deve essere abilitato
        onView(withId(R.id.btnSend)).check(matches(isEnabled()));
    }

    /* ---------- TEST 2 ---------- */
    @Test
    public void clickSend_callsViewModelAndShowsLoading() {

        FragmentScenario.launchInContainer(
                NewChatFragment.class, null,
                R.style.AppTheme, factory).onFragment(f ->
                Navigation.setViewNavController(f.requireView(), nav));

        String prompt = "Nuova chat di prova";
        onView(withId(R.id.editMessage)).perform(typeText(prompt), closeSoftKeyboard());
        onView(withId(R.id.btnSend)).perform(click());

        // verifichiamo la chiamata su *factory.getVm()*
        Mockito.verify(factory.getVm()).send(prompt);

        // simuliamo loading true e controlliamo la progress bar
        factory.loadingLd.postValue(true);
        onView(withId(R.id.lottieLoading)).check(matches(isDisplayed()));
    }

    /* ---------- TEST 3 ---------- */
    @Test
    public void pressBack_navigatesToHomeFragment() {

        /* –––––  Stack realistico  Home → NewChat ––––– */
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
            nav.setGraph(R.navigation.mobile_navigation);
            nav.setCurrentDestination(R.id.navigation_home);   // schermata precedente
            nav.navigate(R.id.fragment_new_chat);              // schermata in test
        });

        /* –––––  Lancia il fragment ––––– */
        FragmentScenario.launchInContainer(
                NewChatFragment.class, null,
                R.style.AppTheme, factory
        ).onFragment(f -> Navigation.setViewNavController(f.requireView(), nav));

        /* –––––  Premi Back  ––––– */
        //onView(isRoot()).perform(pressKey(KEYCODE_BACK));

        /* –––––  Ora dobbiamo aver chiuso l'app  ––––– */
        assertThrows(NoActivityResumedException.class, Espresso::pressBack);
    }
}
