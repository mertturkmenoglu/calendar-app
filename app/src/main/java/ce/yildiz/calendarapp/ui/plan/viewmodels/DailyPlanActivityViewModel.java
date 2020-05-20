package ce.yildiz.calendarapp.ui.plan.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import ce.yildiz.calendarapp.models.Event;
import ce.yildiz.calendarapp.util.Constants;

public class DailyPlanActivityViewModel extends ViewModel {
    private MutableLiveData<List<Event>> mEvents;

    public LiveData<List<Event>> getEvents(int year, int month, int dayOfMonth) {
        if (mEvents == null) {
            mEvents = new MutableLiveData<>();
            loadEvents(year, month, dayOfMonth);
        }

        return mEvents;
    }

    private void loadEvents(final int year, final int month, final int dayOfMonth) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (auth.getCurrentUser() == null) return;

        final List<Event> events = new ArrayList<>();

        Task<QuerySnapshot> result = db.collection(Constants.Collections.USERS)
                .document(auth.getCurrentUser().getUid())
                .collection(Constants.Collections.USER_EVENTS)
                .get();

        result.addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                Event e = document.toObject(Event.class);

                if (e.getStartDate() != null
                        && e.getStartDate().getYear() + 1900 == year
                        && e.getStartDate().getMonth() == month
                        && e.getStartDate().getDate() == dayOfMonth) {
                    events.add(document.toObject(Event.class));
                }
            }

            mEvents.postValue(events);
        });
    }
}
