package ce.yildiz.calendarapp.ui.detail;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ce.yildiz.calendarapp.R;
import ce.yildiz.calendarapp.databinding.ActivityEventDetailBinding;
import ce.yildiz.calendarapp.model.Event;
import ce.yildiz.calendarapp.util.Constants;

@SuppressWarnings("deprecation")
public class EventDetailActivity extends AppCompatActivity {
    private ActivityEventDetailBinding binding;
    private Date startDate;
    private Date endDate;
    private String originalEventName;
    @SuppressWarnings("FieldCanBeLocal")
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String userId;

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

            originalEventName = e.getName();
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

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser() == null) {
            finish();
            return;
        }

        userId = mAuth.getCurrentUser().getUid();

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
                save();
            }
        });
    }

    private void save() {
        final String nameFinal = binding.eventDetailEventName.getText().toString().trim();
        final String detailFinal = binding.eventDetailDetail.getText().toString().trim();
        final Date startDateFinal = startDate;
        final Date endDateFinal = endDate;
        final String[] locationText = binding.eventDetailLocation.getText().toString().trim().split(",");

        GeoPoint location;

        try {
            location = new GeoPoint(
                    Double.parseDouble(locationText[0]),
                    Double.parseDouble(locationText[1])
            );
        } catch (Exception e) {
            binding.eventDetailLocation.setError(getString(R.string.format_error_message));
            binding.eventDetailLocation.requestFocus();
            return;
        }

        final GeoPoint locationFinal = location;

        final String reminderFreqFinal = (String) binding.eventDetailReminderFreq.getSelectedItem();
        final String reminderTypeFinal = (String) binding.eventDetailReminderType.getSelectedItem();
        final String typeFinal = binding.eventDetailType.getText().toString().trim();

        if (originalEventName == null) {
            originalEventName = nameFinal;
        }

        if (TextUtils.isEmpty(nameFinal)) {
            binding.eventDetailEventName.setError(getString(R.string.field_empty_message));
            binding.eventDetailEventName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(detailFinal)) {
            binding.eventDetailDetail.setError(getString(R.string.field_empty_message));
            binding.eventDetailDetail.requestFocus();
            return;
        }

        if (startDateFinal == null) {
            binding.eventDetailStartDate.setError(getString(R.string.field_empty_message));
            binding.eventDetailStartDate.requestFocus();
            return;
        }

        if (endDateFinal == null) {
            binding.eventDetailEndDate.setError(getString(R.string.field_empty_message));
            binding.eventDetailEndDate.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(reminderFreqFinal)) {
            return;
        }

        if (TextUtils.isEmpty(reminderTypeFinal)) {
            return;
        }

        if (TextUtils.isEmpty(typeFinal)) {
            binding.eventDetailType.setError(getString(R.string.field_empty_message));
            binding.eventDetailType.requestFocus();
            return;
        }

        db.collection(Constants.Collections.USERS)
                .document(userId)
                .collection(Constants.Collections.USER_EVENTS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot querySnapshot = task.getResult();
                            if (querySnapshot == null) return;

                            for (DocumentSnapshot s : querySnapshot) {
                                Event e = s.toObject(Event.class);
                                if (e == null) continue;

                                if (e.getName() != null && e.getName().equals(originalEventName)) {
                                    s.getReference().update(
                                            Constants.EventFields.DETAIL, detailFinal,
                                            Constants.EventFields.END_DATE, endDateFinal,
                                            Constants.EventFields.LOCATION, locationFinal,
                                            Constants.EventFields.NAME, nameFinal,
                                            Constants.EventFields.REMINDER_FREQ, reminderFreqFinal,
                                            Constants.EventFields.REMINDER_TYPE, reminderTypeFinal,
                                            Constants.EventFields.START_DATE, startDateFinal,
                                            Constants.EventFields.TYPE, typeFinal
                                    ).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(EventDetailActivity.this,
                                                        R.string.update_ok_message, Toast.LENGTH_SHORT).show();
                                                finish();
                                            } else {
                                                Toast.makeText(EventDetailActivity.this,
                                                        R.string.update_error_message, Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    }
                });
    }
}
