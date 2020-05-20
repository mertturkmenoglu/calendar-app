package ce.yildiz.calendarapp.ui.reminder.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ce.yildiz.calendarapp.R;
import ce.yildiz.calendarapp.interfaces.RecyclerViewClickListener;

public class ReminderListAdapter extends RecyclerView.Adapter<ReminderListAdapter.ReminderViewHolder> {
    private final List<Date> mReminders;
    private final RecyclerViewClickListener mListener;

    public static class ReminderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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

    public ReminderListAdapter(List<Date> reminders, RecyclerViewClickListener listener) {
        this.mReminders = reminders;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ReminderListAdapter.ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.reminder_list_item, parent, false);

        return new ReminderViewHolder(itemView, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, final int position) {
        final Locale locale = new Locale("tr", "TR");
        final Date date = mReminders.get(position);
        final String formattedDate = DateFormat.getDateInstance(DateFormat.DEFAULT, locale)
                .format(date.getTime());

        holder.dateTV.setText(formattedDate);
    }

    @Override
    public int getItemCount() {
        return mReminders.size();
    }
}
