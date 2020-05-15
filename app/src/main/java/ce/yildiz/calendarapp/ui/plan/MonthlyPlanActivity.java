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

import ce.yildiz.calendarapp.databinding.ActivityMonthlyPlanBinding;
import ce.yildiz.calendarapp.interfaces.RecyclerViewClickListener;
import ce.yildiz.calendarapp.model.Event;
import ce.yildiz.calendarapp.ui.detail.EventDetailActivity;
import ce.yildiz.calendarapp.util.Constants;

public class MonthlyPlanActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ActivityMonthlyPlanBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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

                            for (QueryDocumentSnapshot document : snapshot) {
                                events.add(document.toObject(Event.class));
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

                            EventListAdapter adapter = new EventListAdapter(MonthlyPlanActivity.this,
                                    events, listener);
                            binding.monthlyRecyclerView.setAdapter(adapter);
                        }
                    }
                });
    }
}



