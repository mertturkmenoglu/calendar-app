package ce.yildiz.calendarapp.ui.reminder.adapters;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ce.yildiz.calendarapp.R;
import ce.yildiz.calendarapp.interfaces.RecyclerViewClickListener;
import ce.yildiz.calendarapp.models.Event;
import ce.yildiz.calendarapp.util.Constants;
import ce.yildiz.calendarapp.util.NotificationUtil;

public class ReminderListAdapter
        extends RecyclerView.Adapter<ReminderListAdapter.ReminderViewHolder> {
    private static final String TAG = ReminderListAdapter.class.getSimpleName();
    private final List<Date> mReminders;
    private final RecyclerViewClickListener mListener;
    private final Context mContext;

    public static class ReminderViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        final TextView dateTV;
        final RecyclerViewClickListener mListener;

        ReminderViewHolder(View view, RecyclerViewClickListener listener) {
            super(view);
            mListener = listener;
            view.setOnClickListener(this);

            dateTV = view.findViewById(R.id.reminder_list_item_reminder_date);
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v, getAdapterPosition());
        }
    }

    public ReminderListAdapter(Context context,
                               List<Date> reminders, RecyclerViewClickListener listener) {
        this.mContext = context;
        this.mReminders = reminders;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ReminderListAdapter.ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                     int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.reminder_list_item, parent, false);

        return new ReminderViewHolder(itemView, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, final int position) {
        final Locale locale = new Locale(Constants.LOCALE_LANGUAGE, Constants.LOCALE_COUNTRY);
        final Date date = mReminders.get(position);
        final String formattedDate = DateFormat.getDateInstance(DateFormat.DEFAULT, locale)
                .format(date.getTime());

        holder.dateTV.setText(formattedDate);
    }

    @Override
    public int getItemCount() {
        return mReminders.size();
    }

    public void deleteItem(int position, String eventName, String userId) {
        Date removedReminder = mReminders.remove(position);
        notifyItemRemoved(position);

        removeFromDatabase(removedReminder, eventName, userId);
    }

    private void removeFromDatabase(Date reminder, String eventName, String userId) {
        Task<QuerySnapshot> result = FirebaseFirestore.getInstance()
                .collection(Constants.Collections.USERS)
                .document(userId)
                .collection(Constants.Collections.USER_EVENTS)
                .whereEqualTo("name", eventName)
                .get();

        result.addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot s : queryDocumentSnapshots) {
                Event e = s.toObject(Event.class);
                ArrayList<Date> reminders = new ArrayList<>(e.getReminders());

                for (Date d : reminders) {
                    if (d.equals(reminder)) {
                        reminders.remove(d);
                        break;
                    }
                }

                s.getReference().update(Constants.EventFields.REMINDERS, reminders);
                final String reminderType = e.getReminderType();
                final int requestCode = s.getId().hashCode();

                NotificationUtil.cancelRepeatingNotification(mContext, requestCode);

                if (reminderType == null) return;

                if (reminderType.equals(Constants.ReminderTypes.SOUND)) {
                    NotificationUtil.cancelSound(mContext, requestCode);
                } else if (reminderType.equals(Constants.ReminderTypes.VIBRATION)) {
                    NotificationUtil.cancelVibration(mContext, requestCode);
                } else {
                    Log.e(TAG, "Unknown reminder type");
                }

                break;
            }

            Toast.makeText(mContext,
                    R.string.reminder_delete_ok_message, Toast.LENGTH_SHORT).show();
        });

        result.addOnFailureListener(e -> {
            Log.e(TAG, "Reminder delete failed", e);
            Toast.makeText(mContext,
                    R.string.reminder_delete_error_message, Toast.LENGTH_SHORT).show();
        });
    }
}
