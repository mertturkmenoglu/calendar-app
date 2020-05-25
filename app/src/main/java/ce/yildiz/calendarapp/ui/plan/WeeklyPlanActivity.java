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
import ce.yildiz.calendarapp.databinding.ActivityWeeklyPlanBinding;
import ce.yildiz.calendarapp.interfaces.RecyclerViewClickListener;
import ce.yildiz.calendarapp.models.Event;
import ce.yildiz.calendarapp.ui.detail.EventDetailActivity;
import ce.yildiz.calendarapp.ui.plan.adapters.EventListAdapter;
import ce.yildiz.calendarapp.ui.plan.viewmodels.WeeklyPlanActivityViewModel;
import ce.yildiz.calendarapp.util.Constants;
import ce.yildiz.calendarapp.util.SharedPreferencesUtil;

public class WeeklyPlanActivity extends AppCompatActivity {
    private ActivityWeeklyPlanBinding binding;
    @SuppressWarnings("FieldCanBeLocal")
    private WeeklyPlanActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (SharedPreferencesUtil.getTheme().equals(Constants.AppThemes.DARK)) {
            setTheme(R.style.DarkTheme);
        } else {
            setTheme(R.style.AppTheme);
        }

        super.onCreate(savedInstanceState);
        binding = ActivityWeeklyPlanBinding.inflate(getLayoutInflater());
        View root = binding.getRoot();
        setContentView(root);

        viewModel = new ViewModelProvider(this).get(WeeklyPlanActivityViewModel.class);
        viewModel.getEvents().observe(this, this::initRecyclerView);
    }

    private void initRecyclerView(final List<Event>events) {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        binding.weeklyRecyclerView.setLayoutManager(layoutManager);

        RecyclerViewClickListener listener = (view, position) -> {
            Intent eventDetailIntent = new Intent(this, EventDetailActivity.class);
            eventDetailIntent.putExtra("event", new Gson().toJson(events.get(position)));

            startActivity(eventDetailIntent);
        };

        EventListAdapter adapter = new EventListAdapter(this, events, listener);
        binding.weeklyRecyclerView.setAdapter(adapter);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return;

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new EventSwipeToDeleteCallback(adapter, user.getUid())
        );

        itemTouchHelper.attachToRecyclerView(binding.weeklyRecyclerView);

        if (events.isEmpty()) {
            binding.weeklyRecyclerView.setVisibility(View.GONE);
            binding.emptyView.setVisibility(View.VISIBLE);
        } else {
            binding.weeklyRecyclerView.setVisibility(View.VISIBLE);
            binding.emptyView.setVisibility(View.GONE);
        }
    }
}
