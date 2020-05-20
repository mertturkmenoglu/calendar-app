package ce.yildiz.calendarapp.ui.reminder.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.List;

import ce.yildiz.calendarapp.models.Event;
import ce.yildiz.calendarapp.util.Constants;

public class ReminderListActivityViewModel extends ViewModel {
    private MutableLiveData<List<Date>> mReminders;

    public LiveData<List<Date>> getReminders(String eventName) {
        if (mReminders == null) {
            mReminders = new MutableLiveData<>();
            loadEvents(eventName);
        }

        return mReminders;
    }

    private void loadEvents(final String eventName) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (auth.getCurrentUser() == null) return;

        Task<QuerySnapshot> result = db.collection(Constants.Collections.USERS)
                .document(auth.getCurrentUser().getUid())
                .collection(Constants.Collections.USER_EVENTS)
                .get();

        result.addOnSuccessListener(queryDocumentSnapshots -> {
            Event event = null;

            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                Event e = document.toObject(Event.class);

                if (e.getName() != null && e.getName().equals(eventName)) {
                    event = e;
                    break;
                }
            }

            if (event == null) return;

            mReminders.postValue(event.getReminders());
        });
    }
}
