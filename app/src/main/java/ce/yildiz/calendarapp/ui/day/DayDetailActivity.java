package ce.yildiz.calendarapp.ui.day;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import ce.yildiz.calendarapp.databinding.ActivityDayDetailBinding;
import ce.yildiz.calendarapp.modal.User;
import ce.yildiz.calendarapp.util.Constants;

public class DayDetailActivity extends AppCompatActivity {
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
            Toast.makeText(this, "Invalid date", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser() == null) {
            finish();
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();

        final DocumentReference documentReference = db.collection(Constants.Collections.USERS).document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot == null) {
                    return;
                }

                User u = documentSnapshot.toObject(User.class);
                if (u == null) return;

                binding.dayDetailDateText.setText(u.toString());
            }
        });
    }
}
