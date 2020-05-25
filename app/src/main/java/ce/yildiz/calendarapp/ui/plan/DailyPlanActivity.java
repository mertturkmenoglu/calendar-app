package ce.yildiz.calendarapp.ui.plan;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import java.util.List;

import ce.yildiz.calendarapp.R;
import ce.yildiz.calendarapp.databinding.ActivityDailyPlanBinding;
import ce.yildiz.calendarapp.interfaces.RecyclerViewClickListener;
import ce.yildiz.calendarapp.models.Event;
import ce.yildiz.calendarapp.ui.detail.EventDetailActivity;
import ce.yildiz.calendarapp.ui.plan.adapters.EventListAdapter;
import ce.yildiz.calendarapp.ui.plan.viewmodels.DailyPlanActivityViewModel;
import ce.yildiz.calendarapp.ui.reminder.ReminderSwipeToDeleteCallback;
import ce.yildiz.calendarapp.util.Constants;
import ce.yildiz.calendarapp.util.SharedPreferencesUtil;

public class DailyPlanActivity extends AppCompatActivity {
    private ActivityDailyPlanBinding binding;
    @SuppressWarnings("FieldCanBeLocal")
    private DailyPlanActivityViewModel viewModel;

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

        Intent i = getIntent();

        if (i == null) {
            finish();
            return;
        }

        final int year = i.getIntExtra("year", 0);
        final int month = i.getIntExtra("month", -1);
        final int day = i.getIntExtra("day", 0);

        viewModel = new ViewModelProvider(this).get(DailyPlanActivityViewModel.class);
        viewModel.getEvents(year, month, day).observe(this, this::initRecyclerView);

        binding.dailyPlanAddFab.setOnClickListener(v -> {
            // With no extras, event detail activity
            // acts like a new event saver activity
            Intent eventDetailIntent = new Intent(DailyPlanActivity.this,
                    EventDetailActivity.class);

            startActivity(eventDetailIntent);
            finish();
        });
    }

    private void initRecyclerView(final List<Event> events) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.recyclerView.setLayoutManager(layoutManager);

        RecyclerViewClickListener listener = (view, position) -> {
            Intent eventDetailIntent = new Intent(this, EventDetailActivity.class);
            eventDetailIntent.putExtra("event", new Gson().toJson(events.get(position)));

            startActivity(eventDetailIntent);
        };

        EventListAdapter adapter = new EventListAdapter(this, events, listener);
        binding.recyclerView.setAdapter(adapter);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new EventSwipeToDeleteCallback(adapter, user.getUid())
        );

        itemTouchHelper.attachToRecyclerView(binding.recyclerView);

        if (events.isEmpty()) {
            binding.recyclerView.setVisibility(View.GONE);
            binding.emptyView.setVisibility(View.VISIBLE);
        } else {
            binding.recyclerView.setVisibility(View.VISIBLE);
            binding.emptyView.setVisibility(View.GONE);
        }
    }
}
