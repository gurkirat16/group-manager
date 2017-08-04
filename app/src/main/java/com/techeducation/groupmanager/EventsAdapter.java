package com.techeducation.groupmanager;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gurkirat on 17/4/17.
 */

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {

    List<EventsFeed> eventsList;

    public EventsAdapter(List<EventsFeed> eventsList){
        this.eventsList = eventsList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView id,name,date,time,venue,desc;

        public ViewHolder(View itemView) {
            super(itemView);

            id = (TextView)itemView.findViewById(R.id.tvEventId);
            name = (TextView)itemView.findViewById(R.id.tvEventName);
            date = (TextView)itemView.findViewById(R.id.tvEventDate);
            time = (TextView)itemView.findViewById(R.id.tvEventTime);
            venue = (TextView)itemView.findViewById(R.id.txtEvntVenue);
            desc = (TextView)itemView.findViewById(R.id.tvEventDesc);
        }
    }
    @Override
    public EventsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(EventsAdapter.ViewHolder holder, int position) {

        EventsFeed eventsFeed = eventsList.get(position);

        holder.id.setText("#" + Integer.toString(eventsFeed.getId()));
        holder.name.setText(eventsFeed.getName());
        holder.date.setText(eventsFeed.getDate());
        holder.time.setText(eventsFeed.getTime());
        holder.venue.setText(eventsFeed.getVenue());
        holder.desc.setText(eventsFeed.getDesc());

    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }
}