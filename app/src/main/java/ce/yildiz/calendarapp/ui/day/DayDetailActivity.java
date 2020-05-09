package ce.yildiz.calendarapp.ui.day;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ce.yildiz.calendarapp.databinding.ActivityDayDetailBinding;

public class DayDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityDayDetailBinding binding = ActivityDayDetailBinding.inflate(getLayoutInflater());
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

         if (year == 0 || month == 0 || day == 0) {
             Toast.makeText(this, "Invalid date", Toast.LENGTH_SHORT).show();
             finish();
             return;
         }

         String dateText = day + "/" + month + "/" + year;
         binding.dayDetailDateText.setText(dateText);
    }
}
