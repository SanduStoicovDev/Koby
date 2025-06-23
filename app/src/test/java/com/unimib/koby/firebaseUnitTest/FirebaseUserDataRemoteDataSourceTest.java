package com.unimib.koby.firebaseUnitTest;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.unimib.koby.data.source.user.FirebaseUserDataRemoteDataSource;
import com.unimib.koby.model.User;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FirebaseUserDataRemoteDataSourceTest {

    @Test
    public void fetchProfileImageUrl_nullUid_returnsCompletedTask() {
        // 1. Prepara i mock di Firestore & Storage
        FirebaseFirestore mockDb   = mock(FirebaseFirestore.class);
        FirebaseStorage   mockSt   = mock(FirebaseStorage.class);

        try (MockedStatic<FirebaseFirestore> stDb = mockStatic(FirebaseFirestore.class);
             MockedStatic<FirebaseStorage>   stSt = mockStatic(FirebaseStorage.class)) {

            stDb.when(FirebaseFirestore::getInstance).thenReturn(mockDb);
            stSt.when(FirebaseStorage::getInstance).thenReturn(mockSt);

            // 2. Ora puoi creare il tuo data-source senza che Firebase si inizializzi davvero
            FirebaseUserDataRemoteDataSource ds = new FirebaseUserDataRemoteDataSource();

            Task<Uri> t = ds.fetchProfileImageUrl(null);
            assertThat(t.isComplete()).isTrue();
            assertThat(t.getResult()).isNull();
        }
    }
}
