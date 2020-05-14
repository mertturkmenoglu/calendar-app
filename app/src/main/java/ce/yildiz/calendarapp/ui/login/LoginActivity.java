package ce.yildiz.calendarapp.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import ce.yildiz.calendarapp.R;
import ce.yildiz.calendarapp.databinding.ActivityLoginBinding;
import ce.yildiz.calendarapp.ui.main.MainActivity;
import ce.yildiz.calendarapp.ui.register.RegisterActivity;
import ce.yildiz.calendarapp.util.Constants;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        mAuth = FirebaseAuth.getInstance();

        binding.loginForgotPasswordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent passwordResetIntent = new Intent(LoginActivity.this, PasswordResetActivity.class);
                startActivity(passwordResetIntent);
                finish();
            }
        });

        binding.loginSignUpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUpIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(signUpIntent);
                finish();
            }
        });

        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(LoginActivity.this, R.string.login_ok_message, Toast.LENGTH_SHORT).show();
                            Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(mainIntent);
                        } else {
                            Toast.makeText(LoginActivity.this,
                                    R.string.login_error_message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
