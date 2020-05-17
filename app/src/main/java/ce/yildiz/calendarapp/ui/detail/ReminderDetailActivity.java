package ce.yildiz.calendarapp.ui.detail;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ce.yildiz.calendarapp.R;
import ce.yildiz.calendarapp.databinding.ActivityReminderDetailBinding;
import ce.yildiz.calendarapp.models.Event;
import ce.yildiz.calendarapp.util.Constants;
import ce.yildiz.calendarapp.util.NotificationUtil;
import ce.yildiz.calendarapp.util.SharedPreferencesUtil;

public class ReminderDetailActivity extends AppCompatActivity {
    private static final String TAG = ReminderDetailActivity.class.getSimpleName();

    private ActivityReminderDetailBinding binding;
    private Date mDate;
    private Date mOriginalDate;

    private FirebaseFirestore db;
    private String userId;
    private String eventName;

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
        eventName = i.getStringExtra("name");
        userId = i.getStringExtra("userId");
        final Locale locale = new Locale("tr", "TR");

        if (eventName == null || userId == null) return;

        if (timeLong != -1) {
            mDate = new Date(timeLong);
            mOriginalDate = new Date(timeLong);

            String dateText = getString(R.string.date) + " "
                    + DateFormat.getDateInstance(DateFormat.DEFAULT, locale).format(mDate);
            binding.reminderDetailDateText.setText(dateText);

            @SuppressWarnings("deprecation")
            String time = getString(R.string.time) + " " + mDate.getHours() + ":" + mDate.getMinutes();
            binding.reminderDetailTimeText.setText(time);
        } else {
            mDate = new Date();
        }

        db = FirebaseFirestore.getInstance();

        binding.reminderDetailDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(ReminderDetailActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @SuppressWarnings("deprecation")
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                mDate.setYear(year - Constants.DATE_YEAR_DIFF);
                                mDate.setMonth(monthOfYear);
                                mDate.setDate(dayOfMonth);
                                String dateText = getString(R.string.date) + " "
                                        + DateFormat.getDateInstance(DateFormat.DEFAULT, locale).format(mDate);
                                binding.reminderDetailDateText.setText(dateText);
                            }
                        }, year, month, day);
                datePickerDialog.show();
            }
        });

        binding.reminderDetailTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                final int hour = c.get(Calendar.HOUR_OF_DAY);
                final int minute = c.get(Calendar.MINUTE);

                @SuppressWarnings("deprecation")
                TimePickerDialog timePickerDialog = new TimePickerDialog(ReminderDetailActivity.this,
                        android.R.style.Theme_Holo_Dialog_NoActionBar,
                        new TimePickerDialog.OnTimeSetListener() {
                            @SuppressWarnings("deprecation")
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                mDate.setHours(hourOfDay);
                                mDate.setMinutes(minute);
                                String time = getString(R.string.time) + " " + hourOfDay + ":" + minute;
                                binding.reminderDetailTimeText.setText(time);
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
                if (mOriginalDate == null) {
                    save();
                } else {
                    update();
                }
            }
        });

        binding.reminderDetailDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOriginalDate != null) {
                    delete();
                }
            }
        });
    }

    private void save() {
        db.collection(Constants.Collections.USERS)
                .document(userId)
                .collection(Constants.Collections.USER_EVENTS)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot s : queryDocumentSnapshots) {
                            Event e = s.toObject(Event.class);
                            ArrayList<Date> reminders = new ArrayList<>(e.getReminders());
                            reminders.add(mDate);

                            s.getReference().update(Constants.EventFields.REMINDERS, reminders);

                            NotificationUtil.startNotification(
                                    ReminderDetailActivity.this,
                                    mDate,
                                    s.getId().hashCode(),
                                    e.getName(),
                                    e.getDetail()
                            );

                            if (e.getReminderType().equals(Constants.ReminderTypes.VIBRATION)) {
                                NotificationUtil.startVibration(
                                        ReminderDetailActivity.this,
                                        mDate,
                                        s.getId().hashCode()
                                );
                            } else if (e.getReminderType().equals(Constants.ReminderTypes.SOUND)) {
                                NotificationUtil.startSound(
                                        ReminderDetailActivity.this,
                                        mDate,
                                        s.getId().hashCode()
                                );
                            } else {
                                Log.e(TAG, "Unknown reminder type");
                            }

                            break;
                        }

                        Toast.makeText(ReminderDetailActivity.this,
                                R.string.reminder_add_ok_message, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ReminderDetailActivity.this,
                                R.string.reminder_add_error_message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void update() {
        db.collection(Constants.Collections.USERS).document(userId)
                .collection(Constants.Collections.USER_EVENTS)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot s : queryDocumentSnapshots) {
                            Event e = s.toObject(Event.class);

                            if (e.getName().equals(eventName)) {
                                ArrayList<Date> reminders = new ArrayList<>(e.getReminders());

                                for (Date d : reminders) {
                                    if (d.equals(mOriginalDate)) {
                                        reminders.remove(d);
                                        break;
                                    }
                                }

                                reminders.add(mDate);
                                s.getReference().update(Constants.EventFields.REMINDERS, reminders)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(ReminderDetailActivity.this,
                                                        R.string.reminder_update_ok_message, Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(ReminderDetailActivity.this,
                                                        R.string.reminder_update_error_message, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ReminderDetailActivity.this,
                                R.string.reminder_update_error_message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void delete() {
        db.collection(Constants.Collections.USERS)
                .document(userId)
                .collection(Constants.Collections.USER_EVENTS)
                .whereEqualTo("name", eventName)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot s : queryDocumentSnapshots) {
                            Event e = s.toObject(Event.class);
                            ArrayList<Date> reminders = new ArrayList<>(e.getReminders());

                            for (Date d : reminders) {
                                if (d.equals(mDate)) {
                                    reminders.remove(d);
                                    break;
                                }
                            }

                            s.getReference().update(Constants.EventFields.REMINDERS, reminders);
                            final String reminderType = e.getReminderType();
                            final int requestCode = s.getId().hashCode();

                            NotificationUtil.cancelNotification(
                                    ReminderDetailActivity.this,
                                    requestCode
                            );

                            if (reminderType == null) {
                                Log.e(TAG, "Reminder is null");
                                return;
                            }

                            if (reminderType.equals(Constants.ReminderTypes.SOUND)) {
                                NotificationUtil.cancelSound(
                                        ReminderDetailActivity.this,
                                        requestCode
                                );
                            } else if (reminderType.equals(Constants.ReminderTypes.VIBRATION)) {
                                NotificationUtil.cancelVibration(
                                        ReminderDetailActivity.this,
                                        requestCode
                                );
                            } else {
                                Log.e(TAG, "Unknown reminder type");
                            }

                            break;
                        }

                        Toast.makeText(ReminderDetailActivity.this,
                                R.string.reminder_delete_ok_message, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ReminderDetailActivity.this,
                                R.string.reminder_delete_error_message, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
