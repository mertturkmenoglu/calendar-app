package ce.yildiz.calendarapp.ui.plan;

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
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;

import ce.yildiz.calendarapp.databinding.ActivityWeeklyPlanBinding;
import ce.yildiz.calendarapp.interfaces.RecyclerViewClickListener;
import ce.yildiz.calendarapp.model.Event;
import ce.yildiz.calendarapp.ui.detail.EventDetailActivity;
import ce.yildiz.calendarapp.util.Constants;

public class WeeklyPlanActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ActivityWeeklyPlanBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWeeklyPlanBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser() == null) {
            finish();
            return;
        }

        db.collection(Constants.Collections.USERS).document(mAuth.getCurrentUser().getUid())
                .collection(Constants.Collections.USER_EVENTS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            final ArrayList<Event> events = new ArrayList<>();

                            QuerySnapshot snapshot = task.getResult();
                            if (snapshot == null) return;

                            Date currentDate = new Date();
                            Date oneWeekBefore = new Date(currentDate.getTime() - Constants.ONE_WEEK_IN_MILLIS);
                            Date oneWeekAfter = new Date(currentDate.getTime() + Constants.ONE_WEEK_IN_MILLIS);

                            for (QueryDocumentSnapshot document : snapshot) {
                                Event e = document.toObject(Event.class);
                                if (e.getStartDate() != null && e.getStartDate().before(oneWeekAfter) && e.getStartDate().after(oneWeekBefore)) {
                                    events.add(document.toObject(Event.class));
                                }
                            }

                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(WeeklyPlanActivity.this);
                            binding.weeklyRecyclerView.setLayoutManager(layoutManager);

                            RecyclerViewClickListener listener = new RecyclerViewClickListener() {
                                @Override
                                public void onClick(View view, int position) {
                                    Intent eventDetailIntent = new Intent(WeeklyPlanActivity.this,
                                            EventDetailActivity.class);
                                    eventDetailIntent.putExtra("event",
                                            new Gson().toJson(events.get(position)));

                                    startActivity(eventDetailIntent);
                                }
                            };

                            EventListAdapter adapter = new EventListAdapter(WeeklyPlanActivity.this,
                                    events, listener);
                            binding.weeklyRecyclerView.setAdapter(adapter);

                            if (events.isEmpty()) {
                                binding.weeklyRecyclerView.setVisibility(View.GONE);
                                binding.emptyView.setVisibility(View.VISIBLE);
                            } else {
                                binding.weeklyRecyclerView.setVisibility(View.VISIBLE);
                                binding.emptyView.setVisibility(View.GONE);
                            }
                        }
                    }
                });
    }
}
