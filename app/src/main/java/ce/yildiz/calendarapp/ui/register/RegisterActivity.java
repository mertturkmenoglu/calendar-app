package ce.yildiz.calendarapp.ui.register;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        binding.signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = binding.signUpEmailEt.getText().toString().trim();
                final String password = binding.signUpPasswordEt.getText().toString().trim();
                final String githubUsername = binding.signUpGithubUsernameEt.getText().toString().trim();

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

                if (TextUtils.isEmpty(githubUsername)) {
                    binding.signUpGithubUsernameEt.setError(getString(R.string.field_empty_message));
                }

                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            if (mAuth.getCurrentUser() == null) {
                                return;
                            }

                            String userId = mAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = db.collection(Constants.Collections.USERS).document(userId);

                            Map<String, Object> user = new HashMap<>();
                            user.put(Constants.UserFields.EMAIL, email);
                            user.put(Constants.UserFields.GITHUB_USERNAME, githubUsername);

                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(RegisterActivity.this,
                                            R.string.registration_ok_message, Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(RegisterActivity.this,
                                            R.string.registration_error_message, Toast.LENGTH_SHORT).show();
                                }
                            });

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
