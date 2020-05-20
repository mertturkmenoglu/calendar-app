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
import java.util.Date;
import java.util.List;

import ce.yildiz.calendarapp.models.Event;
import ce.yildiz.calendarapp.util.Constants;

public class WeeklyPlanActivityViewModel extends ViewModel {
    private MutableLiveData<List<Event>> mEvents;

    public LiveData<List<Event>> getEvents() {
        if (mEvents == null) {
            mEvents = new MutableLiveData<>();
            loadEvents();
        }

        return mEvents;
    }

    private void loadEvents() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (auth.getCurrentUser() == null) return;

        final List<Event> events = new ArrayList<>();

        Task<QuerySnapshot> result = db.collection(Constants.Collections.USERS)
                .document(auth.getCurrentUser().getUid())
                .collection(Constants.Collections.USER_EVENTS)
                .get();

        result.addOnSuccessListener(queryDocumentSnapshots -> {
            Date currentDate = new Date();
            Date oneWeekBefore = new Date(currentDate.getTime() - Constants.ONE_WEEK_IN_MILLIS);
            Date oneWeekAfter = new Date(currentDate.getTime() + Constants.ONE_WEEK_IN_MILLIS);

            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                Event e = document.toObject(Event.class);

                if (e.getStartDate() != null
                        && e.getStartDate().before(oneWeekAfter)
                        && e.getStartDate().after(oneWeekBefore)) {
                    events.add(e);
                }
            }

            mEvents.postValue(events);
        });
    }
}
