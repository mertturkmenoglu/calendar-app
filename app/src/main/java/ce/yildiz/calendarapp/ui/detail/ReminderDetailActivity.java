package ce.yildiz.calendarapp.ui.detail;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ce.yildiz.calendarapp.R;
import ce.yildiz.calendarapp.databinding.ActivityReminderDetailBinding;
import ce.yildiz.calendarapp.util.Constants;
import ce.yildiz.calendarapp.util.SharedPreferencesUtil;

public class ReminderDetailActivity extends AppCompatActivity {
    private static final String TAG = ReminderDetailActivity.class.getSimpleName();

    private ActivityReminderDetailBinding binding;
    private Date mDate;
    private Date mOriginalDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String t = SharedPreferencesUtil.getTheme();

        if (t == null) {
            setTheme(R.style.AppTheme);
        } else if (t.equals(Constants.AppThemes.DARK)) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }


        super.onCreate(savedInstanceState);
        binding = ActivityReminderDetailBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        Intent i = getIntent();

        if (i == null) return;

        long timeLong = i.getLongExtra("reminder", -1);
        final Locale locale = new Locale("tr", "TR");

        if (timeLong != -1) {
            mDate = new Date(timeLong);
            mOriginalDate = new Date(timeLong);

            binding.reminderDetailDate.setText(
                    DateFormat.getDateInstance(DateFormat.DEFAULT, locale).format(mDate)
            );

            String time = "" + mDate.getHours() + ":" + mDate.getMinutes();
            binding.reminderDetailTime.setText(time);
        } else {
            mDate = new Date();
        }

        binding.reminderDetailDate.setFocusable(false);
        binding.reminderDetailDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(ReminderDetailActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                mDate.setYear(year - 1900);
                                mDate.setMonth(monthOfYear);
                                mDate.setDate(dayOfMonth);
                                binding.reminderDetailDate.setText(
                                        DateFormat.getDateInstance(DateFormat.DEFAULT, locale).format(mDate)
                                );
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        binding.reminderDetailTime.setFocusable(false);
        binding.reminderDetailTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                final int hour = c.get(Calendar.HOUR_OF_DAY);
                final int minute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(ReminderDetailActivity.this,
                        android.R.style.Theme_Holo_Dialog_NoActionBar,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                mDate.setHours(hourOfDay);
                                mDate.setMinutes(minute);
                                String time = "" + hourOfDay + ":" + minute;
                                binding.reminderDetailTime.setText(time);
                            }
                        }, hour, minute, true);

                if (timePickerDialog.getWindow() != null) {
                    timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                }

                timePickerDialog.show();
            }
        });

        binding.reminderDetailSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        binding.reminderDetailDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOriginalDate != null) {
                    Log.d(TAG, "onClick() called with: v = [" + v + "]");
                }
            }
        });
    }
}
