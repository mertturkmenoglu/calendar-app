package ce.yildiz.calendarapp.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import ce.yildiz.calendarapp.R;
import ce.yildiz.calendarapp.databinding.ActivityMainBinding;
import ce.yildiz.calendarapp.ui.login.LoginActivity;
import ce.yildiz.calendarapp.ui.plan.DailyPlanActivity;
import ce.yildiz.calendarapp.ui.plan.MonthlyPlanActivity;
import ce.yildiz.calendarapp.ui.plan.WeeklyPlanActivity;
import ce.yildiz.calendarapp.ui.settings.SettingsActivity;
import ce.yildiz.calendarapp.util.Constants;
import ce.yildiz.calendarapp.util.StringUtil;

public class MainActivity extends AppCompatActivity {
    @SuppressWarnings("FieldCanBeLocal")
    private FirebaseAuth mAuth;
    @SuppressWarnings("FieldCanBeLocal")
    private FirebaseFirestore db;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser() == null) {
            finish();
            return;
        }

        final String userId = mAuth.getCurrentUser().getUid();

        DocumentReference documentReference = db.collection(Constants.Collections.USERS).document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot == null) {
                    return;
                }

                final String email = documentSnapshot.getString(Constants.UserFields.EMAIL);
                final String gUsername = documentSnapshot.getString(Constants.UserFields.GITHUB_USERNAME);
                final String pictureURL = StringUtil.getUserImageURL(gUsername);

                binding.mainEmailText.setText(email);
                Glide.with(MainActivity.this)
                        .load(pictureURL)
                        .override(125, 125)
                        .placeholder(R.drawable.ic_person_holo_purple_24dp)
                        .error(R.drawable.ic_adb_black_24dp)
                        .into(binding.mainUserPicture);
            }
        });

        binding.calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Intent dailyPlanIntent = new Intent(MainActivity.this, DailyPlanActivity.class);
                dailyPlanIntent.putExtra("year", year);
                dailyPlanIntent.putExtra("month", month);
                dailyPlanIntent.putExtra("day", dayOfMonth);
                startActivity(dailyPlanIntent);
            }
        });

        binding.mainWeeklyPlanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent weeklyPlanIntent = new Intent(MainActivity.this, WeeklyPlanActivity.class);
                startActivity(weeklyPlanIntent);
            }
        });

        binding.mainMonthlyPlanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent monthlyPlanIntent = new Intent(MainActivity.this, MonthlyPlanActivity.class);
                startActivity(monthlyPlanIntent);
            }
        });

        binding.mainSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(settingsIntent);
            }
        });

        binding.logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });
    }
}
