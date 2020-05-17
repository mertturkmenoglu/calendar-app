package ce.yildiz.calendarapp.ui.detail;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ce.yildiz.calendarapp.R;
import ce.yildiz.calendarapp.databinding.ActivityEventDetailBinding;
import ce.yildiz.calendarapp.model.Event;
import ce.yildiz.calendarapp.ui.main.MainActivity;
import ce.yildiz.calendarapp.ui.reminder.ReminderListActivity;
import ce.yildiz.calendarapp.util.Constants;
import ce.yildiz.calendarapp.util.NotificationUtil;
import ce.yildiz.calendarapp.util.SharedPreferencesUtil;

@SuppressWarnings("deprecation")
public class EventDetailActivity extends AppCompatActivity {
    private static final String TAG = EventDetailActivity.class.getSimpleName();

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
        String t = SharedPreferencesUtil.getTheme();

        if (t == null) {
            setTheme(R.style.AppTheme);
        } else if (t.equals(Constants.AppThemes.DARK)) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        binding = ActivityEventDetailBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        Intent i = getIntent();

        if (i == null) return;

        String jsonString = i.getStringExtra("event");
        final Locale locale = new Locale("tr", "TR");

        if (jsonString != null) {
            Event e = new Gson().fromJson(jsonString, Event.class);

            originalEventName = e.getName();
            binding.eventDetailEventName.setText(e.getName());
            binding.eventDetailDetail.setText(e.getDetail());

            startDate = e.getStartDate();
            endDate = e.getEndDate();

            binding.eventDetailStartDate.setText(DateFormat.getDateInstance(DateFormat.DEFAULT, locale).format(e.getStartDate()));
            binding.eventDetailEndDate.setText(DateFormat.getDateInstance(DateFormat.DEFAULT, locale).format(e.getEndDate()));

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
                                binding.eventDetailStartDate.setText(DateFormat.getDateInstance(DateFormat.DEFAULT, locale).format(startDate));
                            }
                        }, year, month, day);
                datePickerDialog.show();

                final int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(EventDetailActivity.this, android.R.style.Theme_Holo_Dialog_NoActionBar,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                startDate.setHours(hourOfDay);
                                startDate.setMinutes(minute);
                                binding.eventDetailStartDate.setText(DateFormat.getDateInstance(DateFormat.DEFAULT, locale).format(startDate));
                            }
                        }, hour, minute, true);

                if (timePickerDialog.getWindow() != null) {
                    timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                }

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
                                binding.eventDetailEndDate.setText(DateFormat.getDateInstance(DateFormat.DEFAULT, locale).format(endDate));
                            }
                        }, year, month, day);
                datePickerDialog.show();

                final int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(EventDetailActivity.this, android.R.style.Theme_Holo_Dialog_NoActionBar,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                endDate.setHours(hourOfDay);
                                endDate.setMinutes(minute);
                                binding.eventDetailEndDate.setText(DateFormat.getDateInstance(DateFormat.DEFAULT, locale).format(endDate));
                            }
                        }, hour, minute, true);

                if (timePickerDialog.getWindow() != null) {
                    timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                }

                timePickerDialog.show();
            }
        });

        binding.eventDetailRemindersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openReminders();
            }
        });

        binding.eventDetailSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (originalEventName == null) {
                    save();
                } else {
                    update();
                }
            }
        });

        binding.eventDetailShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
            }
        });

        binding.eventDetailDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (originalEventName != null) {
                    delete();
                }
            }
        });
    }

    private void openReminders() {
        Intent reminderListIntent = new Intent(EventDetailActivity.this,
                ReminderListActivity.class);

        if (originalEventName == null) {
            binding.eventDetailEventName.setError(getString(R.string.event_name_required));
            binding.eventDetailEventName.requestFocus();
            return;
        }

        reminderListIntent.putExtra("name", originalEventName);
        startActivity(reminderListIntent);
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

        Map<String, Object> event = new HashMap<>();
        event.put(Constants.EventFields.DETAIL, detailFinal);
        event.put(Constants.EventFields.END_DATE, endDateFinal);
        event.put(Constants.EventFields.LOCATION, locationFinal);
        event.put(Constants.EventFields.NAME, nameFinal);
        event.put(Constants.EventFields.REMINDER_FREQ, reminderFreqFinal);
        event.put(Constants.EventFields.REMINDER_TYPE, reminderTypeFinal);
        event.put(Constants.EventFields.REMINDERS, new ArrayList<Date>());
        event.put(Constants.EventFields.START_DATE, startDateFinal);
        event.put(Constants.EventFields.TYPE, typeFinal);

        db.collection(Constants.Collections.USERS)
                .document(userId)
                .collection(Constants.Collections.USER_EVENTS)
                .add(event)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        NotificationUtil.startNotification(
                                EventDetailActivity.this,
                                startDateFinal,
                                documentReference.getId().hashCode(),
                                nameFinal,
                                detailFinal
                        );

                        if (reminderTypeFinal.equals(Constants.ReminderTypes.SOUND)) {
                            NotificationUtil.startSound(
                                    EventDetailActivity.this,
                                    startDateFinal,
                                    documentReference.getId().hashCode()
                            );
                        } else if (reminderTypeFinal.equals(Constants.ReminderTypes.VIBRATION)) {
                            NotificationUtil.startVibration(
                                    EventDetailActivity.this,
                                    startDateFinal,
                                    documentReference.getId().hashCode()
                            );
                        } else {
                            Log.e(TAG, "Unknown reminder type");
                        }

                        Toast.makeText(EventDetailActivity.this,
                                R.string.new_event_ok_message, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EventDetailActivity.this,
                                R.string.new_event_error_message, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void update() {
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

    private void share() {
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

        Locale locale = new Locale("tr", "TR");
        String message = "Event Name: " + nameFinal + "\n";
        message += "Event Detail: " + detailFinal + "\n";
        message += "Start Date: " + DateFormat.getDateInstance(DateFormat.DEFAULT, locale).format(startDateFinal) + "\n";
        message += "End Date: " + DateFormat.getDateInstance(DateFormat.DEFAULT, locale).format(endDateFinal) + "\n";
        message += "Location: [" + locationFinal.getLatitude() + ", " + locationFinal.getLongitude() + "]\n";

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
        startActivity(shareIntent);
    }

    private void delete() {
        db.collection(Constants.Collections.USERS)
                .document(userId)
                .collection(Constants.Collections.USER_EVENTS)
                .whereEqualTo("name", originalEventName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(EventDetailActivity.this,
                                    R.string.event_delete_error_message, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        QuerySnapshot snapshot = task.getResult();

                        if (snapshot == null) return;

                        for (QueryDocumentSnapshot documentSnapshot : snapshot) {
                            final int requestCode = documentSnapshot.getId().hashCode();
                            final String reminderType = documentSnapshot.getString(Constants.EventFields.REMINDER_TYPE);

                            documentSnapshot.getReference().delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(EventDetailActivity.this,
                                                    R.string.event_delete_ok_message, Toast.LENGTH_SHORT).show();

                                            NotificationUtil.cancelNotification(
                                                    EventDetailActivity.this,
                                                    requestCode
                                            );

                                            if (reminderType == null) {
                                                Log.e(TAG, "Reminder is null");
                                                return;
                                            }

                                            if (reminderType.equals(Constants.ReminderTypes.SOUND)) {
                                                NotificationUtil.cancelSound(
                                                        EventDetailActivity.this,
                                                        requestCode
                                                );
                                            } else if (reminderType.equals(Constants.ReminderTypes.VIBRATION)) {
                                                NotificationUtil.cancelVibration(
                                                        EventDetailActivity.this,
                                                        requestCode
                                                );
                                            } else {
                                                Log.e(TAG, "Unknown reminder type");
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(EventDetailActivity.this,
                                                    R.string.event_delete_error_message, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });

        Intent mainIntent = new Intent(EventDetailActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
