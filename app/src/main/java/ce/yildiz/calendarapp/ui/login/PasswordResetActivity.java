package ce.yildiz.calendarapp.ui.login;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import ce.yildiz.calendarapp.R;
import ce.yildiz.calendarapp.databinding.ActivityPasswordResetBinding;

public class PasswordResetActivity extends AppCompatActivity {
    private ActivityPasswordResetBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPasswordResetBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        binding.forgotPasswordProgressBar.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();

        binding.forgotPasswordResetPasswordBtn.setOnClickListener(v -> reset());
    }

    private void reset() {
        final String email = binding.forgotPasswordLoginEt.getText().toString().trim();
        binding.forgotPasswordProgressBar.setVisibility(View.VISIBLE);

        mAuth.sendPasswordResetEmail(email).addOnSuccessListener(o -> {
            binding.forgotPasswordProgressBar.setVisibility(View.GONE);

            Toast.makeText(this,
                    R.string.password_reset_link_send_ok_message, Toast.LENGTH_SHORT).show();
            finish();
        }).addOnFailureListener(e -> {
            binding.forgotPasswordProgressBar.setVisibility(View.GONE);

            Toast.makeText(this,
                    R.string.password_reset_link_send_error_message, Toast.LENGTH_SHORT).show();
        });
    }
}
