package com.unimib.koby.data.repository.space;

import android.net.Uri;
import androidx.lifecycle.LiveData;
import com.unimib.koby.model.Result;
import com.unimib.koby.model.SpaceStudy;
import java.util.List;

public interface  ISpaceStudyRepository {
    LiveData<Result> uploadSpaceStudy(Uri pdfUri, String title);
    LiveData<List<SpaceStudy>> fetchMySpaces();
}
