package ce.yildiz.calendarapp.ui.plan.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.List;
import java.util.Locale;

import ce.yildiz.calendarapp.R;
import ce.yildiz.calendarapp.interfaces.RecyclerViewClickListener;
import ce.yildiz.calendarapp.models.Event;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.EventViewHolder> {
    private final List<Event> mEvents;
    private final RecyclerViewClickListener mListener;

    public static class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
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

    public EventListAdapter(List<Event> events, RecyclerViewClickListener listener) {
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
        final Locale locale = new Locale("tr", "TR");
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
}
