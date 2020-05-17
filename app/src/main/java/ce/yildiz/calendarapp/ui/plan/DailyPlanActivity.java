package ce.yildiz.calendarapp.ui.plan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

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
import com.google.gson.Gson;

import java.util.ArrayList;

import ce.yildiz.calendarapp.R;
import ce.yildiz.calendarapp.databinding.ActivityDailyPlanBinding;
import ce.yildiz.calendarapp.interfaces.RecyclerViewClickListener;
import ce.yildiz.calendarapp.models.Event;
import ce.yildiz.calendarapp.ui.detail.EventDetailActivity;
import ce.yildiz.calendarapp.ui.plan.adapters.EventListAdapter;
import ce.yildiz.calendarapp.util.Constants;
import ce.yildiz.calendarapp.util.SharedPreferencesUtil;

public class DailyPlanActivity extends AppCompatActivity {
    private ActivityDailyPlanBinding binding;
    @SuppressWarnings("FieldCanBeLocal")
    private FirebaseAuth mAuth;
    @SuppressWarnings("FieldCanBeLocal")
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (SharedPreferencesUtil.getTheme().equals(Constants.AppThemes.DARK)) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        binding = ActivityDailyPlanBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser() == null) {
            finish();
            return;
        }

        Intent i = getIntent();

        if (i == null) {
            finish();
            return;
        }

        final int year = i.getIntExtra("year", 0);
        final int month = i.getIntExtra("month", -1);
        final int day = i.getIntExtra("day", 0);

        if (year == 0 || month == -1 || day == 0) {
            Toast.makeText(this, R.string.invalid_date, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        db.collection(Constants.Collections.USERS).document(mAuth.getCurrentUser().getUid())
                .collection(Constants.Collections.USER_EVENTS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressWarnings("deprecation")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            final ArrayList<Event> events = new ArrayList<>();

                            QuerySnapshot snapshot = task.getResult();
                            if (snapshot == null) return;

                            for (QueryDocumentSnapshot document : snapshot) {
                                Event e = document.toObject(Event.class);

                                if (e.getStartDate() != null
                                        && e.getStartDate().getYear() + 1900 == year
                                        && e.getStartDate().getMonth() == month
                                        && e.getStartDate().getDate() == day) {
                                    events.add(document.toObject(Event.class));
                                }

                            }

                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(DailyPlanActivity.this);
                            binding.recyclerView.setLayoutManager(layoutManager);

                            RecyclerViewClickListener listener = new RecyclerViewClickListener() {
                                @Override
                                public void onClick(View view, int position) {
                                    Intent eventDetailIntent = new Intent(DailyPlanActivity.this,
                                            EventDetailActivity.class);
                                    eventDetailIntent.putExtra("event",
                                            new Gson().toJson(events.get(position)));

                                    startActivity(eventDetailIntent);
                                }
                            };

                            EventListAdapter adapter = new EventListAdapter(
                                    events, listener);
                            binding.recyclerView.setAdapter(adapter);

                            if (events.isEmpty()) {
                                binding.recyclerView.setVisibility(View.GONE);
                                binding.emptyView.setVisibility(View.VISIBLE);
                            } else {
                                binding.recyclerView.setVisibility(View.VISIBLE);
                                binding.emptyView.setVisibility(View.GONE);
                            }
                        }
                    }
                });

        binding.addNewEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // With no extras, event detail activity
                // acts like a new event saver activity
                Intent eventDetailIntent = new Intent(DailyPlanActivity.this,
                        EventDetailActivity.class);

                startActivity(eventDetailIntent);
                finish();
            }
        });
    }
}
