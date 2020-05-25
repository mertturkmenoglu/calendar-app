package ce.yildiz.calendarapp.ui.plan;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import ce.yildiz.calendarapp.ui.plan.adapters.EventListAdapter;

public class EventSwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
    private EventListAdapter mAdapter;
    private String mUserId;

    EventSwipeToDeleteCallback(EventListAdapter adapter, String userId) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        mAdapter = adapter;
        mUserId = userId;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView,
                          @NonNull RecyclerView.ViewHolder viewHolder,
                          @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();
        mAdapter.deleteItem(position, mUserId);
    }
}
