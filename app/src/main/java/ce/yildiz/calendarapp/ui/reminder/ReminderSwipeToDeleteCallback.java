package ce.yildiz.calendarapp.ui.reminder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import ce.yildiz.calendarapp.ui.reminder.adapters.ReminderListAdapter;

public class ReminderSwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {
    private ReminderListAdapter mAdapter;
    private String mEventName;
    private String mUserId;

    ReminderSwipeToDeleteCallback(ReminderListAdapter adapter, String eventName,
                                         String userId) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        mAdapter = adapter;
        mEventName = eventName;
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
        mAdapter.deleteItem(position, mEventName, mUserId);
    }
}
