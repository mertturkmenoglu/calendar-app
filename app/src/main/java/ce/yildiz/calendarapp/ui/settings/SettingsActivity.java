package ce.yildiz.calendarapp.ui.settings;

import android.app.Activity;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.List;

import ce.yildiz.calendarapp.R;
import ce.yildiz.calendarapp.databinding.ActivitySettingsBinding;
import ce.yildiz.calendarapp.ui.main.MainActivity;
import ce.yildiz.calendarapp.util.Constants;
import ce.yildiz.calendarapp.util.SharedPreferencesUtil;

@SuppressWarnings("CodeBlock2Expr")
public class SettingsActivity extends AppCompatActivity {
    public static final int RINGTONE_REQUEST_CODE = 10;
    private ActivitySettingsBinding binding;

    @SuppressWarnings("FieldCanBeLocal")
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (SharedPreferencesUtil.getTheme().equals(Constants.AppThemes.DARK)) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser() == null) {
            finish();
            return;
        }

        userId = mAuth.getCurrentUser().getUid();

        Task<DocumentSnapshot> result = db.collection(Constants.Collections.USERS)
                .document(userId)
                .get();

        result.addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot == null) return;
            setContents(documentSnapshot);
        });

        result.addOnFailureListener(e -> {
            Toast.makeText(this, R.string.login_error_message, Toast.LENGTH_SHORT).show();
        });

        binding.settingsBackFab.setOnClickListener(v -> {
            Intent mainIntent = new Intent(this, MainActivity.class);
            startActivity(mainIntent);
            finish();
        });
    }

    private void setContents(final DocumentSnapshot documentSnapshot) {
        final String appTheme = documentSnapshot.getString(Constants.UserFields.APP_THEME);
        final String defaultReminderFreq = documentSnapshot.getString(
                Constants.UserFields.DEFAULT_REMINDER_FREQUENCY
        );
        final String defaultSound = documentSnapshot.getString(Constants.UserFields.DEFAULT_SOUND);

        binding.settingsDefaultSound.setText(defaultSound);

        ArrayAdapter<CharSequence> reminderFreqAdapter = ArrayAdapter.createFromResource(
                SettingsActivity.this,
                R.array.reminder_freq,
                android.R.layout.simple_spinner_item
        );

        reminderFreqAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.settingsDefaultReminderFrequencySpinner.setAdapter(reminderFreqAdapter);

        List<String> freqChoices = Arrays.asList(
                getResources().getStringArray(R.array.reminder_freq)
        );
        binding.settingsDefaultReminderFrequencySpinner.setSelection(
                freqChoices.indexOf(defaultReminderFreq)
        );

        ArrayAdapter<CharSequence> appThemeAdapter = ArrayAdapter.createFromResource(
                SettingsActivity.this,
                R.array.app_theme,
                android.R.layout.simple_spinner_item
        );

        appThemeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.settingsAppThemeSpinner.setAdapter(appThemeAdapter);

        List<String> themeChoices = Arrays.asList(getResources().getStringArray(R.array.app_theme));
        binding.settingsAppThemeSpinner.setSelection(themeChoices.indexOf(appTheme));

        binding.settingsSoundButton.setOnClickListener(v -> chooseSound());

        binding.settingsSaveButton.setOnClickListener(v -> save());
    }

    private void chooseSound() {
        String soundText = binding.settingsDefaultSound.getText().toString().trim();
        Uri soundUri = Uri.parse(soundText);

        Intent i = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        i.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
        i.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, getString(R.string.select_sound_message));
        i.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, soundUri);

        startActivityForResult(i, RINGTONE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) return;

        if (resultCode == Activity.RESULT_OK && requestCode == RINGTONE_REQUEST_CODE) {
            Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

            if (uri != null) {
                binding.settingsDefaultSound.setText(uri.toString());
            } else {
                binding.settingsDefaultSound.setText(
                        RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION).toString()
                );
            }
        }
    }

    private void save() {
        binding.settingsSaveButton.setClickable(false);

        final String appThemeLast = (String) binding.settingsAppThemeSpinner.getSelectedItem();
        final String freqLast = (String) binding.settingsDefaultReminderFrequencySpinner
                .getSelectedItem();
        final String soundLast = binding.settingsDefaultSound.getText().toString().trim();

        Task<Void> result = db.collection(Constants.Collections.USERS).document(userId).update(
                Constants.UserFields.APP_THEME, appThemeLast,
                Constants.UserFields.DEFAULT_REMINDER_FREQUENCY, freqLast,
                Constants.UserFields.DEFAULT_SOUND, soundLast
        );

        result.addOnSuccessListener(o -> {
            SharedPreferencesUtil.saveApplicationTheme(
                    SettingsActivity.this,
                    userId,
                    appThemeLast
            );

            Toast.makeText(SettingsActivity.this,
                    R.string.update_ok_message, Toast.LENGTH_SHORT).show();

            binding.settingsSaveButton.setClickable(true);

            Intent mainIntent = new Intent(this, MainActivity.class);
            startActivity(mainIntent);
            finish();
        });

        result.addOnFailureListener(e -> {
            binding.settingsSaveButton.setClickable(true);
            Toast.makeText(this, R.string.update_error_message, Toast.LENGTH_SHORT).show();
        });
    }
}
