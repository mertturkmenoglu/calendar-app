package ce.yildiz.calendarapp.ui.detail;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import ce.yildiz.calendarapp.R;
import ce.yildiz.calendarapp.databinding.ActivityDayDetailBinding;
import ce.yildiz.calendarapp.models.Event;
import ce.yildiz.calendarapp.models.User;
import ce.yildiz.calendarapp.util.Constants;

public class DayDetailActivity extends AppCompatActivity {
    @SuppressWarnings("FieldCanBeLocal")
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ActivityDayDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDayDetailBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        Intent incomingIntent = getIntent();

        if (incomingIntent == null) {
            finish();
            return;
        }

        final int year = incomingIntent.getIntExtra("year", 0);
        final int month = incomingIntent.getIntExtra("month", 0);
        final int day = incomingIntent.getIntExtra("day", 0);

        if (year == 0 || month == -1 || day == 0) {
            Toast.makeText(this, R.string.invalid_date, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser() == null) {
            finish();
            return;
        }

        final String userId = mAuth.getCurrentUser().getUid();

        // Get User
        final DocumentReference documentReference = db.collection(Constants.Collections.USERS).document(userId);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot == null) return;

                    final String email = documentSnapshot.getString(Constants.UserFields.EMAIL);
                    final String gUsername = documentSnapshot.getString(Constants.UserFields.GITHUB_USERNAME);
                    final String defaultSound = documentSnapshot.getString(Constants.UserFields.DEFAULT_SOUND);
                    final String defaultReminderFreq = documentSnapshot.getString(Constants.UserFields.DEFAULT_REMINDER_FREQUENCY);
                    final String appTheme = documentSnapshot.getString(Constants.UserFields.APP_THEME);

                    // Get events
                    db.collection(Constants.Collections.USERS).document(userId)
                            .collection(Constants.Collections.USER_EVENTS)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        QuerySnapshot result = task.getResult();
                                        if (result == null) return;
                                        ArrayList<Event> events = new ArrayList<>();

                                        for (QueryDocumentSnapshot d : result) {
                                            events.add(d.toObject(Event.class));
                                        }

                                        // Create user
                                        User u = new User(email, gUsername, events,
                                                defaultSound, defaultReminderFreq, appTheme);
                                        binding.dayDetailDateText.setText(u.toString());
                                    }
                                }
                            });
                }
            }
        });
    }
}
