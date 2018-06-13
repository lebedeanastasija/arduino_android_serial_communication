package com.example.anastasiya.arduinoserialcom.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.anastasiya.arduinoserialcom.R;

import java.util.List;

public class ScheduleListAdapter extends RecyclerView.Adapter<ScheduleListAdapter.ViewHolder> {
    private List<String> subjects;
    private List<String> times;
    private List<String> rooms;
    private Context context;
    private Resources res;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTime;
        public TextView tvSubject;
        public TextView tvRoom;
        public View vSchedule;

        public ViewHolder(View v) {
            super(v);
            vSchedule = v;
            tvTime = (TextView) v.findViewById(R.id.tvScheduleTime);
            tvSubject = (TextView) v.findViewById(R.id.tvScheduleSubject);
            tvRoom = (TextView) v.findViewById(R.id.tvScheduleRoom);
        }


    }

    public ScheduleListAdapter(List<String> times, List<String> subjects, List<String> rooms, Context ctx, Activity activity) {
        this.subjects = subjects;
        this.times = times;
        this.rooms = rooms;
        this.context = ctx;
        this.res = context.getResources();
    }

    @Override
    public ScheduleListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_list_item, parent, false);
        ScheduleListAdapter.ViewHolder vh = new ScheduleListAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ScheduleListAdapter.ViewHolder holder, int position) {
        holder.tvTime.setText(times.get(position));
        holder.tvSubject.setText(subjects.get(position));
        holder.tvRoom.setText(rooms.get(position));
    }

    @Override
    public int getItemCount() {
        return times.size();
    }

    @Override
    public  long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
