package ce.yildiz.calendarapp.ui.reminder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import ce.yildiz.calendarapp.R;
import ce.yildiz.calendarapp.databinding.ActivityReminderListBinding;
import ce.yildiz.calendarapp.interfaces.RecyclerViewClickListener;
import ce.yildiz.calendarapp.models.Event;
import ce.yildiz.calendarapp.ui.detail.ReminderDetailActivity;
import ce.yildiz.calendarapp.ui.reminder.adapters.ReminderListAdapter;
import ce.yildiz.calendarapp.util.Constants;
import ce.yildiz.calendarapp.util.SharedPreferencesUtil;

public class ReminderListActivity extends AppCompatActivity {
    private ActivityReminderListBinding binding;
    @SuppressWarnings("FieldCanBeLocal")
    private FirebaseAuth mAuth;
    @SuppressWarnings("FieldCanBeLocal")
    private FirebaseFirestore db;
    private Event mEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String t = SharedPreferencesUtil.getTheme();

        if (t == null) {
            setTheme(R.style.AppTheme);
        } else if (t.equals(Constants.AppThemes.DARK)) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        binding = ActivityReminderListBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser() == null) {
            finish();
            return;
        }

        Intent i = getIntent();

        if (i == null) return;

        final String name = i.getStringExtra("name");

        if (name == null) return;

        final String userId = mAuth.getCurrentUser().getUid();

        db.collection(Constants.Collections.USERS).document(userId)
                .collection(Constants.Collections.USER_EVENTS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressWarnings("ConstantConditions")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot snapshot = task.getResult();
                            if (snapshot == null) return;

                            for (QueryDocumentSnapshot document : snapshot) {
                                Event e = document.toObject(Event.class);

                                if (e != null && e.getName().equals(name)) {
                                    mEvent = e;
                                    break;
                                }
                            }

                            if (mEvent == null) return;

                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ReminderListActivity.this);
                            binding.recyclerView.setLayoutManager(layoutManager);

                            RecyclerViewClickListener listener = new RecyclerViewClickListener() {
                                @Override
                                public void onClick(View view, int position) {
                                    Intent reminderDetailIntent = new Intent(ReminderListActivity.this,
                                            ReminderDetailActivity.class);

                                    reminderDetailIntent.putExtra("reminder",
                                            mEvent.getReminders().get(position).getTime());

                                    reminderDetailIntent.putExtra("name", name);
                                    reminderDetailIntent.putExtra("userId", userId);

                                    startActivity(reminderDetailIntent);
                                }
                            };

                            ReminderListAdapter adapter = new ReminderListAdapter(
                                    mEvent.getReminders(), listener);
                            binding.recyclerView.setAdapter(adapter);

                            if (mEvent.getReminders().isEmpty()) {
                                binding.recyclerView.setVisibility(View.GONE);
                                binding.emptyView.setVisibility(View.VISIBLE);
                            } else {
                                binding.recyclerView.setVisibility(View.VISIBLE);
                                binding.emptyView.setVisibility(View.GONE);
                            }
                        }
                    }
                });

        binding.addNewReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reminderDetailIntent = new Intent(ReminderListActivity.this,
                        ReminderDetailActivity.class);

                reminderDetailIntent.putExtra("name", name);
                reminderDetailIntent.putExtra("userId", userId);

                startActivity(reminderDetailIntent);
                finish();
            }
        });
    }
}
