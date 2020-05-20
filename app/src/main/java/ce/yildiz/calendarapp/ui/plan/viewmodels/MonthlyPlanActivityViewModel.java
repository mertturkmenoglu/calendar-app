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

public class MonthlyPlanActivityViewModel extends ViewModel {
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
            Date oneMonthBefore = new Date(currentDate.getTime() - Constants.ONE_MONTH_IN_MILLIS);
            Date oneMonthAfter = new Date(currentDate.getTime() + Constants.ONE_MONTH_IN_MILLIS);

            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                Event e = document.toObject(Event.class);

                if (e.getStartDate() != null
                        && e.getStartDate().before(oneMonthAfter)
                        && e.getStartDate().after(oneMonthBefore)) {
                    events.add(e);
                }
            }

            mEvents.postValue(events);
        });
    }
}
