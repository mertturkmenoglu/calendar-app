package ce.yildiz.calendarapp.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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
import ce.yildiz.calendarapp.util.Constants;
import ce.yildiz.calendarapp.util.SharedPreferencesUtil;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        binding.loginLoginProgressBar.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();

        // Check if user is logged in
        if (mAuth.getCurrentUser() != null) {
            SharedPreferencesUtil.loadApplicationTheme(this, mAuth.getCurrentUser().getUid());

            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);

            startActivity(mainIntent);
            finish();
            return;
        }

        binding.loginForgotPasswordText.setOnClickListener(v -> {
            Intent passwordResetIntent = new Intent(LoginActivity.this,
                    PasswordResetActivity.class);
            startActivity(passwordResetIntent);
        });

        binding.loginSignUpText.setOnClickListener(v -> {
            Intent signUpIntent = new Intent(LoginActivity.this,
                    RegisterActivity.class);
            startActivity(signUpIntent);
        });

        binding.loginButton.setOnClickListener(v -> login());
    }

    private void login() {
        final String email = binding.loginLoginEt.getText().toString().trim();
        final String password = binding.loginPasswordEt.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            binding.loginLoginEt.setError(getString(R.string.field_empty_message));
            return;
        }

        if (TextUtils.isEmpty(password)) {
            binding.loginPasswordEt.setError(getString(R.string.field_empty_message));
            return;
        }

        if (password.length() < Constants.MIN_PASSWORD_LENGTH) {
            binding.loginPasswordEt.setError(getString(R.string.password_short_error));
            return;
        }

        binding.loginLoginProgressBar.setVisibility(View.VISIBLE);

        Task<AuthResult> loginTask = mAuth.signInWithEmailAndPassword(email, password);

        loginTask.addOnSuccessListener(o -> {
            binding.loginLoginProgressBar.setVisibility(View.GONE);

            if (mAuth.getCurrentUser() == null) return;

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
