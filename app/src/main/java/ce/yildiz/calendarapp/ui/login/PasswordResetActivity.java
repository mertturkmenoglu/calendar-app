package ce.yildiz.calendarapp.ui.login;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import ce.yildiz.calendarapp.R;
import ce.yildiz.calendarapp.databinding.ActivityPasswordResetBinding;

public class PasswordResetActivity extends AppCompatActivity {
    private static final String TAG = PasswordResetActivity.class.getSimpleName();

    private ActivityPasswordResetBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPasswordResetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.passwordResetProgressBar.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();

        binding.passwordResetButton.setOnClickListener(v -> reset());
    }

    private void reset() {
        final EditText emailEditText = binding.passwordResetEmail.getEditText();

        if (emailEditText == null) {
            Log.e(TAG, "View not found");
            return;
        }

        final String email = emailEditText.getText().toString().trim();
        binding.passwordResetProgressBar.setVisibility(View.VISIBLE);

        Task<Void> task = mAuth.sendPasswordResetEmail(email);

        task.addOnSuccessListener(o -> {
            binding.passwordResetProgressBar.setVisibility(View.GONE);

            Toast.makeText(this,
                    R.string.password_reset_link_send_ok_message, Toast.LENGTH_SHORT).show();
            finish();
        });

        task.addOnFailureListener(e -> {
            binding.passwordResetProgressBar.setVisibility(View.GONE);

            Toast.makeText(this,
                    R.string.password_reset_link_send_error_message, Toast.LENGTH_SHORT).show();
        });
    }
}
