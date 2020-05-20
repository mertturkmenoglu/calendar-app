package ce.yildiz.calendarapp.ui.register;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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

@SuppressWarnings("CodeBlock2Expr")
public class RegisterActivity extends AppCompatActivity {
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
        View root = binding.getRoot();
        setContentView(root);

        binding.signUpProgressBar.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        binding.signUpButton.setOnClickListener(v -> register());
    }

    private void register() {
        if (!fieldsCorrect()) {
            return;
        }

        binding.signUpProgressBar.setVisibility(View.VISIBLE);

        Task<AuthResult> result = mAuth.createUserWithEmailAndPassword(mEmail, mPassword);

        result.addOnSuccessListener(authResult -> {
            mAuth.signInWithEmailAndPassword(mEmail, mPassword).addOnSuccessListener(authResult1 -> {
                saveToDatabase();

                binding.signUpProgressBar.setVisibility(View.GONE);
                Intent mainIntent = new Intent(this, MainActivity.class);
                startActivity(mainIntent);
            });
        });

        result.addOnFailureListener(e -> {
            Toast.makeText(this,
                    R.string.registration_error_message, Toast.LENGTH_SHORT).show();
        });
    }

    private void saveToDatabase() {
        if (mAuth.getCurrentUser() == null) {
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();
        DocumentReference documentReference = db.collection(Constants.Collections.USERS).document(userId);

        Map<String, Object> user = new HashMap<>();
        user.put(Constants.UserFields.EMAIL, mEmail);
        user.put(Constants.UserFields.GITHUB_USERNAME, mGithubUsername);
        user.put(Constants.UserFields.DEFAULT_SOUND, Constants.DEFAULT_SOUND);
        user.put(Constants.UserFields.DEFAULT_REMINDER_FREQUENCY, Constants.DEFAULT_REMINDER_FREQUENCY);
        user.put(Constants.UserFields.APP_THEME, Constants.AppThemes.DARK);

        Task<Void> result = documentReference.set(user);

        result.addOnSuccessListener(o -> {
            Toast.makeText(RegisterActivity.this,
                    R.string.registration_ok_message, Toast.LENGTH_SHORT).show();
        });

        result.addOnFailureListener(e -> {
            Toast.makeText(RegisterActivity.this,
                    R.string.registration_error_message, Toast.LENGTH_SHORT).show();
        });

        Map<String, Object> events = new HashMap<>();

        documentReference.collection(Constants.Collections.USER_EVENTS)
                .document().set(events)
                .addOnFailureListener(e -> {
                    Toast.makeText(RegisterActivity.this,
                            R.string.registration_error_message, Toast.LENGTH_SHORT).show();
                });
    }

    private boolean fieldsCorrect() {
        mEmail = binding.signUpEmailEt.getText().toString().trim();
        mPassword = binding.signUpPasswordEt.getText().toString().trim();
        mGithubUsername = binding.signUpGithubUsernameEt.getText().toString().trim();

        if (TextUtils.isEmpty(mEmail)) {
            binding.signUpEmailEt.setError(getString(R.string.field_empty_message));
            return false;
        }

        if (TextUtils.isEmpty(mPassword)) {
            binding.signUpPasswordEt.setError(getString(R.string.field_empty_message));
            return false;
        }

        if (mPassword.length() < Constants.MIN_PASSWORD_LENGTH) {
            binding.signUpPasswordEt.setError(getString(R.string.password_short_error));
            return false;
        }

        if (TextUtils.isEmpty(mGithubUsername)) {
            binding.signUpGithubUsernameEt.setError(getString(R.string.field_empty_message));
            return false;
        }

        return true;
    }
}
