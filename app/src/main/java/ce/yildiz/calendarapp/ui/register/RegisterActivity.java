package ce.yildiz.calendarapp.ui.register;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import ce.yildiz.calendarapp.R;
import ce.yildiz.calendarapp.databinding.ActivityRegisterBinding;
import ce.yildiz.calendarapp.ui.main.MainActivity;
import ce.yildiz.calendarapp.util.Constants;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() != null) {
            Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(mainIntent);
            finish();
        }

        binding.signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = binding.signUpEmailEt.getText().toString().trim();
                final String password = binding.signUpPasswordEt.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    binding.signUpEmailEt.setError(getString(R.string.field_empty_message));
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    binding.signUpPasswordEt.setError(getString(R.string.field_empty_message));
                    return;
                }

                if (password.length() < Constants.MIN_PASSWORD_LENGTH) {
                    binding.signUpPasswordEt.setError(getString(R.string.password_short_error));
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this,
                                    R.string.registration_ok_message, Toast.LENGTH_SHORT).show();
                            Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(mainIntent);
                        } else {
                            Toast.makeText(RegisterActivity.this,
                                    R.string.registration_error_message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
}
