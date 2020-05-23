package ce.yildiz.calendarapp.ui.detail;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
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
import ce.yildiz.calendarapp.models.Event;
import ce.yildiz.calendarapp.ui.main.MainActivity;
import ce.yildiz.calendarapp.ui.reminder.ReminderListActivity;
import ce.yildiz.calendarapp.util.Constants;
import ce.yildiz.calendarapp.util.EventUtil;
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

    private FusedLocationProviderClient mFusedLocationClient;
    private Locale mLocale = new Locale("tr", "TR");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setApplicationTheme();

        super.onCreate(savedInstanceState);
        binding = ActivityEventDetailBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        Intent i = getIntent();

        if (i == null) return;

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        String jsonString = i.getStringExtra("event");

        if (jsonString != null) {
            loadData(jsonString);
        } else {
            createViews();
        }

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser() == null) {
            finish();
            return;
        }

        userId = mAuth.getCurrentUser().getUid();

        binding.eventDetailChangeStartDateButton.setOnClickListener(v -> changeStartDate());

        binding.eventDetailChangeEndDateButton.setOnClickListener(v -> changeEndDate());

        binding.eventDetailLocationButton.setOnClickListener(v -> getLocation());

        binding.eventDetailRemindersButton.setOnClickListener(v -> openReminders());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.event_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.event_menu_delete: {
                if (originalEventName != null) {
                    delete();
                }

                break;
            }

            case R.id.event_menu_share: {
                share();
                break;
            }

            case R.id.event_menu_save: {
                if (originalEventName == null) {
                    save();
                } else {
                    update();
                }
                break;
            }

            default:
                break;
        }

        return true;
    }

    private void setApplicationTheme() {
        String t = SharedPreferencesUtil.getTheme();

        if (t == null) {
            setTheme(R.style.AppTheme);
        } else if (t.equals(Constants.AppThemes.DARK)) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }
    }

    private void loadData(@NonNull String jsonString) {
        Event e = new Gson().fromJson(jsonString, Event.class);

        originalEventName = e.getName();
        binding.eventDetailEventName.setText(e.getName());
        binding.eventDetailDetail.setText(e.getDetail());

        startDate = e.getStartDate();
        endDate = e.getEndDate();

        final String formattedStartDate = DateFormat.getDateInstance(DateFormat.DEFAULT, mLocale)
                .format(e.getStartDate());

        final String formattedEndDate = DateFormat.getDateInstance(DateFormat.DEFAULT, mLocale)
                .format(e.getEndDate());

        binding.eventDetailStartDate.setText(formattedStartDate);
        binding.eventDetailEndDate.setText(formattedEndDate);

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
    }

    private void createViews() {
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

    private void changeStartDate() {
        final Calendar c = Calendar.getInstance();
        int currentYear = c.get(Calendar.YEAR);
        int currentMonth = c.get(Calendar.MONTH);
        int currentDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            startDate.setYear(year - Constants.DATE_YEAR_DIFF);
            startDate.setMonth(month);
            startDate.setDate(dayOfMonth);

            final String formattedDate = DateFormat.getDateInstance(DateFormat.DEFAULT, mLocale)
                    .format(startDate);

            binding.eventDetailStartDate.setText(formattedDate);
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                dateSetListener,
                currentYear,
                currentMonth,
                currentDay
        );

        datePickerDialog.show();

        // < ---------    --------- >
        // < ---------    --------- >
        // < ---------    --------- >

        final int currentHour = c.get(Calendar.HOUR_OF_DAY);
        int currentMinute = c.get(Calendar.MINUTE);

        TimePickerDialog.OnTimeSetListener timeSetListener = (view, hour, minute) -> {
            startDate.setHours(hour);
            startDate.setMinutes(minute);

            final String formattedDate = DateFormat.getDateInstance(DateFormat.DEFAULT, mLocale)
                    .format(startDate);

            binding.eventDetailStartDate.setText(formattedDate);
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                android.R.style.Theme_Holo_Dialog_NoActionBar,
                timeSetListener,
                currentHour,
                currentMinute,
                true
        );

        if (timePickerDialog.getWindow() != null) {
            timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        timePickerDialog.show();
    }

    private void changeEndDate() {
        final Calendar c = Calendar.getInstance();
        int currentYear = c.get(Calendar.YEAR);
        int currentMonth = c.get(Calendar.MONTH);
        int currentDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            endDate.setYear(year - Constants.DATE_YEAR_DIFF);
            endDate.setMonth(month);
            endDate.setDate(dayOfMonth);

            final String formattedDate = DateFormat.getDateInstance(DateFormat.DEFAULT, mLocale)
                    .format(endDate);

            binding.eventDetailEndDate.setText(formattedDate);
        };

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                dateSetListener,
                currentYear,
                currentMonth,
                currentDay
        );

        datePickerDialog.show();

        // < ---------    --------- >
        // < ---------    --------- >
        // < ---------    --------- >

        final int currentHour = c.get(Calendar.HOUR_OF_DAY);
        int currentMinute = c.get(Calendar.MINUTE);

        TimePickerDialog.OnTimeSetListener timeSetListener = (view, hour, minute) -> {
            endDate.setHours(hour);
            endDate.setMinutes(minute);

            final String formattedDate = DateFormat.getDateInstance(DateFormat.DEFAULT, mLocale)
                    .format(endDate);

            binding.eventDetailEndDate.setText(formattedDate);
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                android.R.style.Theme_Holo_Dialog_NoActionBar,
                timeSetListener,
                currentHour,
                currentMinute,
                true
        );

        if (timePickerDialog.getWindow() != null) {
            timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        timePickerDialog.show();
    }

    private void openReminders() {
        Intent reminderListIntent = new Intent(this, ReminderListActivity.class);

        if (originalEventName == null) {
            binding.eventDetailEventName.setError(getString(R.string.event_name_required));
            binding.eventDetailEventName.requestFocus();
            return;
        }

        reminderListIntent.putExtra("name", originalEventName);
        startActivity(reminderListIntent);
    }

    @SuppressWarnings("CodeBlock2Expr")
    private void save() {
        final String nameFinal = binding.eventDetailEventName.getText().toString().trim();
        final String detailFinal = binding.eventDetailDetail.getText().toString().trim();
        final Date startDateFinal = startDate;
        final Date endDateFinal = endDate;
        final String[] locationText = binding.eventDetailLocation.getText()
                .toString()
                .trim()
                .split(",");

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

        Task<DocumentReference> result = db.collection(Constants.Collections.USERS)
                .document(userId)
                .collection(Constants.Collections.USER_EVENTS)
                .add(event);

        result.addOnSuccessListener(documentReference -> {
            final int requestCode = documentReference.get().hashCode();

            NotificationUtil.startRepeatingNotification(
                    this,
                    startDateFinal,
                    requestCode,
                    nameFinal,
                    detailFinal,
                    reminderFreqFinal
            );

            if (reminderTypeFinal.equals(Constants.ReminderTypes.SOUND)) {
                NotificationUtil.startSound(this, startDateFinal, requestCode);
            } else if (reminderTypeFinal.equals(Constants.ReminderTypes.VIBRATION)) {
                NotificationUtil.startVibration(this, startDateFinal, requestCode);
            } else {
                Log.e(TAG, "Unknown reminder type");
            }

            Toast.makeText(this,
                    R.string.new_event_ok_message, Toast.LENGTH_SHORT).show();
            finish();
        });

        result.addOnFailureListener(e -> {
            Toast.makeText(this,
                    R.string.new_event_error_message, Toast.LENGTH_SHORT).show();
        });
    }

    private void update() {
        final String nameFinal = binding.eventDetailEventName.getText().toString().trim();
        final String detailFinal = binding.eventDetailDetail.getText().toString().trim();
        final Date startDateFinal = startDate;
        final Date endDateFinal = endDate;
        final String[] locationText = binding.eventDetailLocation.getText()
                .toString()
                .trim()
                .split(",");

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

        Task<QuerySnapshot> result = db.collection(Constants.Collections.USERS)
                .document(userId)
                .collection(Constants.Collections.USER_EVENTS)
                .get();

        result.addOnSuccessListener(queryDocumentSnapshots -> {
            for (DocumentSnapshot s : queryDocumentSnapshots) {
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
                    ).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this,
                                    R.string.update_ok_message, Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(this,
                                    R.string.update_error_message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void share() {
        Event event = new Event();

        final String name = binding.eventDetailEventName.getText().toString().trim();

        if (originalEventName == null) {
            originalEventName = name;
        }

        if (TextUtils.isEmpty(name)) {
            binding.eventDetailEventName.setError(getString(R.string.field_empty_message));
            binding.eventDetailEventName.requestFocus();
            return;
        }

        event.setName(name);

        final String detail = binding.eventDetailDetail.getText().toString().trim();

        if (TextUtils.isEmpty(detail)) {
            binding.eventDetailDetail.setError(getString(R.string.field_empty_message));
            binding.eventDetailDetail.requestFocus();
            return;
        }

        event.setDetail(detail);
        event.setStartDate(startDate);
        event.setEndDate(endDate);

        GeoPoint location = EventUtil.getLocationFromText(
                binding.eventDetailLocation.getText().toString()
        );

        if (location == null) {
            binding.eventDetailLocation.setError(getString(R.string.format_error_message));
            binding.eventDetailLocation.requestFocus();
            return;
        }

        event.setLocation(location);

        String message = EventUtil.getShareableEventMessage(this, mLocale, event);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);

        shareIntent.setType("text/plain");
        shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
        startActivity(shareIntent);
    }

    @SuppressWarnings("CodeBlock2Expr")
    private void delete() {
        Task<QuerySnapshot> result = db.collection(Constants.Collections.USERS)
                .document(userId)
                .collection(Constants.Collections.USER_EVENTS)
                .whereEqualTo("name", originalEventName)
                .get();

        result.addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                final int requestCode = documentSnapshot.getId().hashCode();
                final String reminderType = documentSnapshot.getString(Constants.EventFields.REMINDER_TYPE);

                Task<Void> deleteResult = documentSnapshot.getReference().delete();

                deleteResult.addOnSuccessListener(o -> {
                    Toast.makeText(this,
                            R.string.event_delete_ok_message, Toast.LENGTH_SHORT).show();

                    NotificationUtil.cancelRepeatingNotification(this, requestCode);

                    if (reminderType == null) {
                        Log.e(TAG, "Reminder is null");
                        return;
                    }

                    if (reminderType.equals(Constants.ReminderTypes.SOUND)) {
                        NotificationUtil.cancelSound(this, requestCode);
                    } else if (reminderType.equals(Constants.ReminderTypes.VIBRATION)) {
                        NotificationUtil.cancelVibration(this, requestCode);
                    } else {
                        Log.e(TAG, "Unknown reminder type");
                    }
                });

                deleteResult.addOnFailureListener(e -> {
                    Toast.makeText(this,
                            R.string.event_delete_error_message, Toast.LENGTH_SHORT).show();
                });
            }
        });

        result.addOnFailureListener(e -> {
            Toast.makeText(this,
                    R.string.event_delete_error_message, Toast.LENGTH_SHORT).show();
        });

        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    private void getLocation() {
        if (!checkLocationPermissions()) {
            requestLocationPermissions();
            return;
        }

        if (!isLocationEnabled()) {
            Intent locationEnableIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(locationEnableIntent);
            return;
        }

        mFusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
            Location location = task.getResult();

            if (location == null) {
                requestLocation();
                return;
            }

            final String locationText = location.getLatitude() + ","
                    + location.getLongitude();
            binding.eventDetailLocation.setText(locationText);
        });
    }

    private boolean checkLocationPermissions() {
        int hasAccessFineLocation = ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);

        int hasAccessCoarseLocation = ActivityCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        return (hasAccessCoarseLocation == PackageManager.PERMISSION_GRANTED)
                && (hasAccessFineLocation == PackageManager.PERMISSION_GRANTED);
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (locationManager == null) return false;

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void requestLocation() {
        LocationRequest req = new LocationRequest();
        req.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        req.setInterval(0);
        req.setFastestInterval(0);
        req.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                req,
                new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {

                    }
                },
                Looper.myLooper()
        );
    }

    private void requestLocationPermissions() {
        String[] permissions = new String[] {
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
        };

        ActivityCompat.requestPermissions(this, permissions, Constants.PERMISSION_ID);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Constants.PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            }
        }
    }
}
