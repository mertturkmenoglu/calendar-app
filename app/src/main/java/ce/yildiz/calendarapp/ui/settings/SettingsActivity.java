package ce.yildiz.calendarapp.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
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

public class SettingsActivity extends AppCompatActivity {
    private ActivitySettingsBinding binding;
    @SuppressWarnings("FieldCanBeLocal")
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (SharedPreferencesUtil.getTheme().equals(Constants.AppThemes.DARK)) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser() == null) {
            finish();
            return;
        }

        final String userId = mAuth.getCurrentUser().getUid();

        db.collection(Constants.Collections.USERS).document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot == null) return;

                    final String appTheme = documentSnapshot.getString("appTheme");
                    final String defaultReminderFreq = documentSnapshot.getString("defaultReminderFreq");
                    final String defaultSound = documentSnapshot.getString("defaultSound");

                    binding.settingsDefaultSound.setText(defaultSound);

                    ArrayAdapter<CharSequence> reminderFreqAdapter = ArrayAdapter.createFromResource(
                            SettingsActivity.this,
                            R.array.reminder_freq,
                            android.R.layout.simple_spinner_item
                    );

                    reminderFreqAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.settingsDefaultReminderFrequencySpinner.setAdapter(reminderFreqAdapter);

                    List<String> freqChoices = Arrays.asList(getResources().getStringArray(R.array.reminder_freq));
                    binding.settingsDefaultReminderFrequencySpinner.setSelection(freqChoices.indexOf(defaultReminderFreq));

                    ArrayAdapter<CharSequence> appThemeAdapter = ArrayAdapter.createFromResource(
                            SettingsActivity.this,
                            R.array.app_theme,
                            android.R.layout.simple_spinner_item
                    );

                    appThemeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.settingsAppThemeSpinner.setAdapter(appThemeAdapter);

                    List<String> themeChoices = Arrays.asList(getResources().getStringArray(R.array.app_theme));
                    binding.settingsAppThemeSpinner.setSelection(themeChoices.indexOf(appTheme));

                    binding.settingsSaveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final String appThemeLast = (String) binding.settingsAppThemeSpinner.getSelectedItem();
                            final String freqLast = (String) binding.settingsDefaultReminderFrequencySpinner.getSelectedItem();
                            final String soundLast = binding.settingsDefaultSound.getText().toString().trim();

                            db.collection(Constants.Collections.USERS).document(userId).update(
                                Constants.UserFields.APP_THEME, appThemeLast,
                                    Constants.UserFields.DEFAULT_REMINDER_FREQUENCY, freqLast,
                                    Constants.UserFields.DEFAULT_SOUND, soundLast
                            ).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        SharedPreferencesUtil.saveApplicationTheme(
                                                SettingsActivity.this,
                                                userId,
                                                appThemeLast
                                        );
                                        Toast.makeText(SettingsActivity.this,
                                                R.string.update_ok_message, Toast.LENGTH_SHORT).show();

                                        Intent mainIntent = new Intent(SettingsActivity.this,
                                                MainActivity.class);
                                        startActivity(mainIntent);
                                        finish();
                                    } else {
                                        Toast.makeText(SettingsActivity.this,
                                                R.string.update_error_message, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                } else {
                    Toast.makeText(SettingsActivity.this,
                            R.string.login_error_message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
