package ce.yildiz.calendarapp.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import ce.yildiz.calendarapp.R;
import ce.yildiz.calendarapp.databinding.ActivityLoginBinding;
import ce.yildiz.calendarapp.ui.main.MainActivity;
import ce.yildiz.calendarapp.ui.register.RegisterActivity;
import ce.yildiz.calendarapp.util.SharedPreferencesUtil;
import ce.yildiz.calendarapp.util.Validate;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();

    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.loginLoginProgressBar.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();

        // Check if user is logged in
        // Go to MainActivity
        if (mAuth.getCurrentUser() != null) {
            SharedPreferencesUtil.loadApplicationTheme(this, mAuth.getCurrentUser().getUid());

            Intent mainIntent = new Intent(this, MainActivity.class);

            startActivity(mainIntent);
            finish();

            return;
        }

        binding.loginForgotPasswordText.setOnClickListener(v -> {
            Intent passwordResetIntent = new Intent(this, PasswordResetActivity.class);
            startActivity(passwordResetIntent);
        });

        binding.loginSignUpText.setOnClickListener(v -> {
            Intent signUpIntent = new Intent(this, RegisterActivity.class);
            startActivity(signUpIntent);
        });

        binding.loginButton.setOnClickListener(v -> login());
    }

    private void login() {
        final EditText emailEditText = binding.loginEmailInput.getEditText();
        final EditText passwordEditText = binding.loginPasswordInput.getEditText();

        if (emailEditText == null || passwordEditText == null) {
            Log.e(TAG, "Views not found");
            return;
        }

        final String email = emailEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();

        if (!Validate.validateEmail(email)) {
            binding.loginEmailInput.setError(getString(R.string.field_empty_message));
            return;
        }

        if (!Validate.validatePassword(password)) {
            binding.loginPasswordInput.setError(getString(R.string.password_short_error));
            return;
        }

        binding.loginLoginProgressBar.setVisibility(View.VISIBLE);

        Task<AuthResult> loginTask = mAuth.signInWithEmailAndPassword(email, password);

        loginTask.addOnSuccessListener(o -> {
            binding.loginLoginProgressBar.setVisibility(View.GONE);

            if (mAuth.getCurrentUser() == null) return;

            // Application theme must be set before MainActivity starts
            SharedPreferencesUtil.loadApplicationTheme(this, mAuth.getCurrentUser().getUid());
            Toast.makeText(this, R.string.login_ok_message, Toast.LENGTH_SHORT).show();

            Intent mainIntent = new Intent(this, MainActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainIntent);
        });

        loginTask.addOnFailureListener(e -> {
            binding.loginLoginProgressBar.setVisibility(View.GONE);

            Toast.makeText(this,
                    R.string.login_error_message, Toast.LENGTH_SHORT).show();
        });
    }
}
