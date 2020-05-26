package ce.yildiz.calendarapp.ui.register;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import ce.yildiz.calendarapp.R;
import ce.yildiz.calendarapp.databinding.ActivityRegisterBinding;
import ce.yildiz.calendarapp.ui.main.MainActivity;
import ce.yildiz.calendarapp.util.Constants;
import ce.yildiz.calendarapp.util.Validate;

@SuppressWarnings("CodeBlock2Expr")
public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = RegisterActivity.class.getSimpleName();

    private ActivityRegisterBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String mEmail;
    private String mPassword;
    private String mGithubUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.signUpProgressBar.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        binding.registerButton.setOnClickListener(v -> register());
    }

    private void register() {
        if (!fieldsCorrect()) {
            return;
        }

        binding.signUpProgressBar.setVisibility(View.VISIBLE);

        Task<AuthResult> result = mAuth.createUserWithEmailAndPassword(mEmail, mPassword);

        result.addOnSuccessListener(authResult -> {
            mAuth.signInWithEmailAndPassword(mEmail, mPassword).addOnSuccessListener(res -> {
                saveToDatabase();

                binding.signUpProgressBar.setVisibility(View.GONE);
                Intent mainIntent = new Intent(this, MainActivity.class);
                startActivity(mainIntent);
            });
        });

        result.addOnFailureListener(e -> {
            binding.signUpProgressBar.setVisibility(View.GONE);

            Toast.makeText(this,
                    R.string.registration_error_message, Toast.LENGTH_SHORT).show();
        });
    }

    private void saveToDatabase() {
        if (mAuth.getCurrentUser() == null) {
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        DocumentReference documentReference = db.collection(Constants.Collections.USERS)
                .document(userId);

        Map<String, Object> user = new HashMap<>();
        user.put(Constants.UserFields.EMAIL, mEmail);
        user.put(Constants.UserFields.GITHUB_USERNAME, mGithubUsername);
        user.put(Constants.UserFields.DEFAULT_SOUND, Constants.DEFAULT_SOUND);
        user.put(Constants.UserFields.DEFAULT_REMINDER_FREQUENCY,
                Constants.DEFAULT_REMINDER_FREQUENCY);
        user.put(Constants.UserFields.APP_THEME, Constants.AppThemes.DARK);

        Task<Void> result = documentReference.set(user);

        result.addOnSuccessListener(o -> {
            Toast.makeText(this,
                    R.string.registration_ok_message, Toast.LENGTH_SHORT).show();
        });

        result.addOnFailureListener(e -> {
            Toast.makeText(this,
                    R.string.registration_error_message, Toast.LENGTH_SHORT).show();
        });

        Map<String, Object> events = new HashMap<>();

        documentReference.collection(Constants.Collections.USER_EVENTS)
                .document().set(events)
                .addOnFailureListener(e -> {
                    Toast.makeText(this,
                            R.string.registration_error_message, Toast.LENGTH_SHORT).show();
                });
    }

    private boolean fieldsCorrect() {
        final EditText emailEditText = binding.registerEmail.getEditText();
        final EditText passwordEditText = binding.registerPassword.getEditText();
        final EditText githubUsernameEditText = binding.registerGithubUsername.getEditText();

        if (emailEditText == null || passwordEditText == null || githubUsernameEditText == null) {
            Log.e(TAG, "Views not found");
            return false;
        }

        mEmail = emailEditText.getText().toString().trim();
        mPassword = passwordEditText.getText().toString().trim();
        mGithubUsername = githubUsernameEditText.getText().toString().trim();

        if (Validate.validateEmail(mEmail)) {
            binding.registerEmail.setError(getString(R.string.field_empty_message));
            return false;
        }

        if (Validate.validatePassword(mPassword)) {
            binding.registerPassword.setError(getString(R.string.password_short_error));
            return false;
        }

        if (Validate.validateGithubUsername(mGithubUsername)) {
            binding.registerGithubUsername.setError(getString(R.string.field_empty_message));
            return false;
        }

        return true;
    }
}
