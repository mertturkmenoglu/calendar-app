package ce.yildiz.calendarapp.ui.plan.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.util.List;
import java.util.Locale;

import ce.yildiz.calendarapp.R;
import ce.yildiz.calendarapp.interfaces.RecyclerViewClickListener;
import ce.yildiz.calendarapp.models.Event;
import ce.yildiz.calendarapp.util.Constants;
import ce.yildiz.calendarapp.util.NotificationUtil;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.EventViewHolder> {
    private static final String TAG = EventListAdapter.class.getSimpleName();

    private final List<Event> mEvents;
    private final RecyclerViewClickListener mListener;
    private final Context mContext;

    public static class EventViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        final TextView nameTV;
        final TextView detailTV;
        final TextView startDateTV;
        final RecyclerViewClickListener mListener;

        EventViewHolder(View view, RecyclerViewClickListener listener) {
            super(view);
            mListener = listener;
            view.setOnClickListener(this);

            nameTV = view.findViewById(R.id.event_list_item_name);
            detailTV = view.findViewById(R.id.event_list_item_detail);
            startDateTV = view.findViewById(R.id.event_list_item_start_date);
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v, getAdapterPosition());
        }
    }

    public EventListAdapter(Context context, List<Event> events,
                            RecyclerViewClickListener listener) {
        this.mContext = context;
        this.mEvents = events;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.event_list_item, parent, false);

        return new EventViewHolder(itemView, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, final int position) {
        Event event = mEvents.get(position);
        final Locale locale = new Locale(Constants.LOCALE_LANGUAGE, Constants.LOCALE_COUNTRY);
        final String formattedDate = DateFormat.getDateInstance(DateFormat.DEFAULT, locale)
                .format(event.getStartDate());

        holder.nameTV.setText(event.getName());
        holder.detailTV.setText(event.getDetail());
        holder.startDateTV.setText(formattedDate);
    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    public void deleteItem(int position, String userId) {
        Event removedEvent = mEvents.remove(position);
        notifyItemRemoved(position);
        removeFromDatabase(removedEvent, userId);
    }

    private void removeFromDatabase(Event event, String userId) {
        Task<QuerySnapshot> result = FirebaseFirestore.getInstance()
                .collection(Constants.Collections.USERS)
                .document(userId)
                .collection(Constants.Collections.USER_EVENTS)
                .whereEqualTo("name", event.getName())
                .get();

        result.addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                final int requestCode = documentSnapshot.getId().hashCode();
                final String reminderType = documentSnapshot.getString(
                        Constants.EventFields.REMINDER_TYPE
                );

                Task<Void> deleteResult = documentSnapshot.getReference().delete();

                deleteResult.addOnSuccessListener(o -> {
                    Toast.makeText(mContext,
                            R.string.event_delete_ok_message, Toast.LENGTH_SHORT).show();

                    NotificationUtil.cancelRepeatingNotification(mContext, requestCode);

                    if (reminderType == null) {
                        Log.e(TAG, "Reminder is null");
                        return;
                    }

                    if (reminderType.equals(Constants.ReminderTypes.SOUND)) {
                        NotificationUtil.cancelSound(mContext, requestCode);
                    } else if (reminderType.equals(Constants.ReminderTypes.VIBRATION)) {
                        NotificationUtil.cancelVibration(mContext, requestCode);
                    } else {
                        Log.e(TAG, "Unknown reminder type");
                    }
                });

                deleteResult.addOnFailureListener(e -> {
                    Log.e(TAG, "Event delete failed", e);
                    Toast.makeText(mContext,
                            R.string.event_delete_error_message, Toast.LENGTH_SHORT).show();
                });
            }
        });

        result.addOnFailureListener(e -> {
            Log.e(TAG, "Event delete failed", e);
            Toast.makeText(mContext,
                    R.string.event_delete_error_message, Toast.LENGTH_SHORT).show();
        });
    }
}
