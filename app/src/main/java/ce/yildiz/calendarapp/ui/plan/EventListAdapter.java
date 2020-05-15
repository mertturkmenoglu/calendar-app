package ce.yildiz.calendarapp.ui.plan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ce.yildiz.calendarapp.R;
import ce.yildiz.calendarapp.interfaces.RecyclerViewClickListener;
import ce.yildiz.calendarapp.model.Event;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.EventViewHolder> {
    private final Context mContext;
    private List<Event> mEvents;
    private RecyclerViewClickListener mListener;

    static class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView nameTV;
        TextView detailTV;
        TextView startDateTV;
        TextView endDateTV;
        TextView typeTV;
        RecyclerViewClickListener mListener;

        EventViewHolder(View view, RecyclerViewClickListener listener) {
            super(view);
            mListener = listener;
            view.setOnClickListener(this);

            nameTV = view.findViewById(R.id.event_list_item_name);
            detailTV = view.findViewById(R.id.event_list_item_detail);
            startDateTV = view.findViewById(R.id.event_list_item_start_date);
            endDateTV = view.findViewById(R.id.event_list_item_end_date);
            typeTV = view.findViewById(R.id.event_list_item_type);
        }

        @Override
        public void onClick(View v) {
            mListener.onClick(v, getAdapterPosition());
        }
    }

    EventListAdapter(Context context, List<Event> events, RecyclerViewClickListener listener) {
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
        holder.nameTV.setText(event.getName());
        holder.detailTV.setText(event.getDetail());
        holder.startDateTV.setText(event.getStartDate().toString());
        holder.endDateTV.setText(event.getEndDate().toString());
        holder.typeTV.setText(event.getType());
    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }
}
