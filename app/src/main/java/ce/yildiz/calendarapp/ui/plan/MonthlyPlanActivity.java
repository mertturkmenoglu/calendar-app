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

import ce.yildiz.calendarapp.R;
import ce.yildiz.calendarapp.databinding.ActivityMonthlyPlanBinding;
import ce.yildiz.calendarapp.interfaces.RecyclerViewClickListener;
import ce.yildiz.calendarapp.model.Event;
import ce.yildiz.calendarapp.ui.adapters.EventListAdapter;
import ce.yildiz.calendarapp.ui.detail.EventDetailActivity;
import ce.yildiz.calendarapp.util.Constants;
import ce.yildiz.calendarapp.util.SharedPreferencesUtil;

public class MonthlyPlanActivity extends AppCompatActivity {
    @SuppressWarnings("FieldCanBeLocal")
    private FirebaseAuth mAuth;
    @SuppressWarnings("FieldCanBeLocal")
    private FirebaseFirestore db;
    private ActivityMonthlyPlanBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (SharedPreferencesUtil.getTheme().equals(Constants.AppThemes.DARK)) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        binding = ActivityMonthlyPlanBinding.inflate(getLayoutInflater());
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
                            Date oneMonthBefore = new Date(currentDate.getTime() - Constants.ONE_MONTH_IN_MILLIS);
                            Date oneMonthAfter = new Date(currentDate.getTime() + Constants.ONE_MONTH_IN_MILLIS);

                            for (QueryDocumentSnapshot document : snapshot) {
                                Event e = document.toObject(Event.class);
                                if (e.getStartDate() != null && e.getStartDate().before(oneMonthAfter) && e.getStartDate().after(oneMonthBefore)) {
                                    events.add(document.toObject(Event.class));
                                }
                            }

                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(MonthlyPlanActivity.this);
                            binding.monthlyRecyclerView.setLayoutManager(layoutManager);

                            RecyclerViewClickListener listener = new RecyclerViewClickListener() {
                                @Override
                                public void onClick(View view, int position) {
                                    Intent eventDetailIntent = new Intent(MonthlyPlanActivity.this,
                                            EventDetailActivity.class);
                                    eventDetailIntent.putExtra("event",
                                            new Gson().toJson(events.get(position)));

                                    startActivity(eventDetailIntent);
                                }
                            };

                            EventListAdapter adapter = new EventListAdapter(
                                    events, listener);
                            binding.monthlyRecyclerView.setAdapter(adapter);

                            if (events.isEmpty()) {
                                binding.monthlyRecyclerView.setVisibility(View.GONE);
                                binding.emptyView.setVisibility(View.VISIBLE);
                            } else {
                                binding.monthlyRecyclerView.setVisibility(View.VISIBLE);
                                binding.emptyView.setVisibility(View.GONE);
                            }
                        }
                    }
                });
    }
}



