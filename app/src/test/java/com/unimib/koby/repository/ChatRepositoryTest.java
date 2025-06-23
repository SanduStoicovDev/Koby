package com.unimib.koby.repository;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;

import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.unimib.koby.data.repository.chat.ChatRepository;
import com.unimib.koby.model.Chat;
import com.unimib.koby.model.ChatMessage;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/** Unit-test JVM per ChatRepository (Robolectric + Mockito-inline). */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 33, manifest = Config.NONE)
public class ChatRepositoryTest {

    @Rule public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

    @ClassRule public static final MockedStatic<FirebaseAuth>      sAuth = Mockito.mockStatic(FirebaseAuth.class);
    @ClassRule public static final MockedStatic<FirebaseFirestore> sFs   = Mockito.mockStatic(FirebaseFirestore.class);

    private FirebaseUser        fUser;
    private CollectionReference chatColl;
    private ChatRepository repo;

    @Before
    public void setUp() {
        fUser = mock(FirebaseUser.class);
        when(fUser.getUid()).thenReturn("UID123");
        FirebaseAuth auth = mock(FirebaseAuth.class);
        when(auth.getCurrentUser()).thenReturn(fUser);
        sAuth.when(FirebaseAuth::getInstance).thenReturn(auth);

        FirebaseFirestore fs = mock(FirebaseFirestore.class);
        chatColl = mock(CollectionReference.class, RETURNS_DEEP_STUBS);
        when(fs.collection("users").document("UID123").collection("chats"))
                .thenReturn(chatColl);
        sFs.when(FirebaseFirestore::getInstance).thenReturn(fs);

        repo = new ChatRepository();
    }

    /* ---------- createChat: successo ---------- */
    @Test
    public void createChat_success_returnsChatId() throws Exception {
        Chat chat = new Chat("Nuova chat", Timestamp.now().toString());
        ChatMessage first = new ChatMessage("user", "ciao");

        TaskCompletionSource<DocumentReference> tcs = new TaskCompletionSource<>();
        when(chatColl.add(any())).thenReturn(tcs.getTask());

        var future = repo.createChat(chat, List.of(first));

        DocumentReference doc = mock(DocumentReference.class);
        when(doc.getId()).thenReturn("XYZ");
        CollectionReference msgColl = mock(CollectionReference.class);
        when(doc.collection("messages")).thenReturn(msgColl);

        tcs.setResult(doc);

        assertThat(future.get(1, TimeUnit.SECONDS)).isEqualTo("XYZ");
        verify(msgColl).add(first);
    }

    /* ---------- createChat: failure ---------- */
    @Test
    public void createChat_failure_completesExceptionally() throws Exception {
        Chat chat = new Chat("Err chat", Timestamp.now().toString());
        TaskCompletionSource<DocumentReference> tcs = new TaskCompletionSource<>();
        when(chatColl.add(any())).thenReturn(tcs.getTask());

        var future = repo.createChat(chat, List.of());
        tcs.setException(new RuntimeException("boom"));

        try {
            future.get(1, TimeUnit.SECONDS);
        } catch (Exception e) {
            assertThat(e.getCause().getMessage()).isEqualTo("boom");
        }
    }

    /* ---------- messagesOf: snapshot ---------- */
    @Test
    public void messagesOf_snapshot_updatesLiveData() throws Exception {
        String chatId = "XYZ";
        CollectionReference msgColl = mock(CollectionReference.class);
        when(chatColl.document(chatId).collection("messages")).thenReturn(msgColl);

        Query ordered = mock(Query.class);
        when(msgColl.orderBy(anyString(), any(Query.Direction.class))).thenReturn(ordered);

        @SuppressWarnings("unchecked")
        var captor = org.mockito.ArgumentCaptor.forClass(EventListener.class);
        when(ordered.addSnapshotListener(captor.capture())).thenReturn(null);

        LiveData<List<ChatMessage>> live = repo.messagesOf(chatId);

        ChatMessage m1 = new ChatMessage("user",   "hi");
        ChatMessage m2 = new ChatMessage("system", "hey");

        DocumentSnapshot d1 = mock(DocumentSnapshot.class);
        when(d1.toObject(ChatMessage.class)).thenReturn(m1);
        when(d1.getId()).thenReturn("m1");

        DocumentSnapshot d2 = mock(DocumentSnapshot.class);
        when(d2.toObject(ChatMessage.class)).thenReturn(m2);
        when(d2.getId()).thenReturn("m2");

        QuerySnapshot snap = mock(QuerySnapshot.class);
        when(snap.getDocuments()).thenReturn(Arrays.asList(d1, d2));

        captor.getValue().onEvent(snap, null);

        List<ChatMessage> emitted = getOrAwait(live);
        assertThat(emitted).containsExactly(m1, m2);
    }

    /* helper LiveData */
    private static <T> T getOrAwait(LiveData<T> live)
            throws InterruptedException, TimeoutException {

        Object[] box = new Object[1];
        CountDownLatch latch = new CountDownLatch(1);
        live.observeForever(v -> { box[0] = v; latch.countDown(); });

        if (!latch.await(2, TimeUnit.SECONDS)) {
            throw new TimeoutException("LiveData non emessa");
        }
        //noinspection unchecked
        return (T) box[0];
    }

    @AfterClass
    public static void closeStatics() {
        sAuth.close();
        sFs.close();
    }
}
