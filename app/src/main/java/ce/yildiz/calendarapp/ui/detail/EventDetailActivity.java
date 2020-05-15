package ce.yildiz.calendarapp.ui.detail;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ce.yildiz.calendarapp.R;
import ce.yildiz.calendarapp.databinding.ActivityEventDetailBinding;
import ce.yildiz.calendarapp.model.Event;

@SuppressWarnings("deprecation")
public class EventDetailActivity extends AppCompatActivity {
    private ActivityEventDetailBinding binding;
    private Date startDate;
    private Date endDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEventDetailBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        Intent i = getIntent();

        if (i != null) {
            String jsonString = i.getStringExtra("event");
            Event e = new Gson().fromJson(jsonString, Event.class);

            binding.eventDetailEventName.setText(e.getName());
            binding.eventDetailDetail.setText(e.getDetail());

            startDate = e.getStartDate();
            endDate = e.getEndDate();

            binding.eventDetailStartDate.setText(e.getStartDate().toGMTString());
            binding.eventDetailEndDate.setText(e.getEndDate().toGMTString());

            String location = e.getLocation().getLatitude() + "," + e.getLocation().getLongitude();
            binding.eventDetailLocation.setText(location);

            ArrayAdapter<CharSequence> reminderFreqAdapter = ArrayAdapter.createFromResource(
                    this,
                    R.array.reminder_freq,
                    android.R.layout.simple_spinner_item
            );

            reminderFreqAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.eventDetailReminderFreq.setAdapter(reminderFreqAdapter);

            List<String> freqChoices = Arrays.asList(getResources().getStringArray(R.array.reminder_freq));
            binding.eventDetailReminderFreq.setSelection(freqChoices.indexOf(e.getReminderFreq()));

            ArrayAdapter<CharSequence> reminderTypeAdapter = ArrayAdapter.createFromResource(
                    this,
                    R.array.reminder_type,
                    android.R.layout.simple_spinner_item
            );

            reminderTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.eventDetailReminderType.setAdapter(reminderTypeAdapter);

            List<String> typeChoices = Arrays.asList(getResources().getStringArray(R.array.reminder_type));
            binding.eventDetailReminderType.setSelection(typeChoices.indexOf(e.getReminderType()));

            binding.eventDetailType.setText(e.getType());
        } else {
            startDate = new Date();
            endDate = new Date();

            ArrayAdapter<CharSequence> reminderFreqAdapter = ArrayAdapter.createFromResource(
                    this,
                    R.array.reminder_freq,
                    android.R.layout.simple_spinner_item
            );

            reminderFreqAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.eventDetailReminderFreq.setAdapter(reminderFreqAdapter);

            ArrayAdapter<CharSequence> reminderTypeAdapter = ArrayAdapter.createFromResource(
                    this,
                    R.array.reminder_type,
                    android.R.layout.simple_spinner_item
            );

            reminderTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.eventDetailReminderType.setAdapter(reminderTypeAdapter);
        }

        binding.eventDetailChangeStartDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(EventDetailActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                startDate.setYear(year - 1900);
                                startDate.setMonth(monthOfYear);
                                startDate.setDate(dayOfMonth);
                                binding.eventDetailStartDate.setText(startDate.toGMTString());
                            }
                        }, year, month, day);
                datePickerDialog.show();

                final int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(EventDetailActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                startDate.setHours(hourOfDay);
                                startDate.setMinutes(minute);
                                binding.eventDetailStartDate.setText(startDate.toGMTString());
                            }
                        }, hour, minute, false);
                timePickerDialog.show();
            }
        });

        binding.eventDetailChangeEndDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(EventDetailActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                endDate.setYear(year - 1900);
                                endDate.setMonth(monthOfYear);
                                endDate.setDate(dayOfMonth);
                                binding.eventDetailEndDate.setText(endDate.toGMTString());
                            }
                        }, year, month, day);
                datePickerDialog.show();

                final int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(EventDetailActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                endDate.setHours(hourOfDay);
                                endDate.setMinutes(minute);
                                binding.eventDetailEndDate.setText(endDate.toGMTString());
                            }
                        }, hour, minute, true);
                timePickerDialog.show();
            }
        });

        binding.eventDetailSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(EventDetailActivity.this,
                        "Save Button Clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
