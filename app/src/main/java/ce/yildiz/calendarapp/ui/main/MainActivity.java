package ce.yildiz.calendarapp.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import ce.yildiz.calendarapp.databinding.ActivityMainBinding;
import ce.yildiz.calendarapp.ui.day.DayDetailActivity;
import ce.yildiz.calendarapp.ui.login.LoginActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        binding.calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Intent dayDetailIntent = new Intent(MainActivity.this, DayDetailActivity.class);
                dayDetailIntent.putExtra("year", year);
                dayDetailIntent.putExtra("month", month);
                dayDetailIntent.putExtra("day", dayOfMonth);
                startActivity(dayDetailIntent);
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
