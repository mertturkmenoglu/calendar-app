package ce.yildiz.calendarapp.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import ce.yildiz.calendarapp.R;
import ce.yildiz.calendarapp.databinding.ActivityMainBinding;
import ce.yildiz.calendarapp.ui.login.LoginActivity;
import ce.yildiz.calendarapp.ui.plan.DailyPlanActivity;
import ce.yildiz.calendarapp.ui.plan.MonthlyPlanActivity;
import ce.yildiz.calendarapp.ui.plan.WeeklyPlanActivity;
import ce.yildiz.calendarapp.ui.settings.SettingsActivity;
import ce.yildiz.calendarapp.util.Constants;
import ce.yildiz.calendarapp.util.SharedPreferencesUtil;
import ce.yildiz.calendarapp.util.StringUtil;

public class MainActivity extends AppCompatActivity {
    @SuppressWarnings("FieldCanBeLocal")
    private FirebaseAuth mAuth;
    @SuppressWarnings("FieldCanBeLocal")
    private FirebaseFirestore db;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser() == null) {
            finish();
            return;
        }

        final String userId = mAuth.getCurrentUser().getUid();

        if (SharedPreferencesUtil.getTheme() == null) {
            SharedPreferencesUtil.loadApplicationTheme(this, userId);
        }

        if (SharedPreferencesUtil.getTheme().equals(Constants.AppThemes.DARK)) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        DocumentReference documentReference = db.collection(Constants.Collections.USERS)
                .document(userId);

        documentReference.addSnapshotListener(this, (documentSnapshot, e) -> {
            if (documentSnapshot == null) {
                return;
            }

            final String email = documentSnapshot.getString(Constants.UserFields.EMAIL);
            final String gUsername = documentSnapshot.
                    getString(Constants.UserFields.GITHUB_USERNAME);
            final String pictureURL = StringUtil.getUserImageURL(gUsername);

            binding.mainEmailText.setText(email);
            Glide.with(MainActivity.this)
                    .load(pictureURL)
                    .override(125, 125)
                    .apply(new RequestOptions().circleCrop())
                    .placeholder(R.drawable.ic_person_holo_purple_24dp)
                    .error(R.drawable.ic_adb_black_24dp)
                    .into(binding.mainUserPicture);
        });

        binding.calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Intent dailyPlanIntent = new Intent(this, DailyPlanActivity.class);
            dailyPlanIntent.putExtra("year", year);
            dailyPlanIntent.putExtra("month", month);
            dailyPlanIntent.putExtra("day", dayOfMonth);
            startActivity(dailyPlanIntent);
        });

        binding.mainWeeklyPlanText.setOnClickListener(v -> {
            Intent weeklyPlanIntent = new Intent(this, WeeklyPlanActivity.class);
            startActivity(weeklyPlanIntent);
        });

        binding.mainMonthlyPlanText.setOnClickListener(v -> {
            Intent monthlyPlanIntent = new Intent(this, MonthlyPlanActivity.class);
            startActivity(monthlyPlanIntent);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                finish();
                break;
            case R.id.menu_logout:
                FirebaseAuth.getInstance().signOut();
                Intent loginIntent = new Intent(this, LoginActivity.class);
                startActivity(loginIntent);
                finish();
                break;
            default:
                break;
        }

        return true;
    }
}
