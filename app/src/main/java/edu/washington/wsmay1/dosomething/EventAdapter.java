package edu.washington.wsmay1.dosomething;

/**
 * Created by henrydchipmantyemill on 5/28/15.
 */


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Adapter to bind a Event List to a view
 */
public class EventAdapter extends ArrayAdapter<Event> {
    private Activity activity;
    private ArrayList<Event> events;
    private static LayoutInflater inflater = null;

    public EventAdapter (Activity activity, int textViewResourceId,ArrayList<Event> events) {
        super(activity, textViewResourceId, events);
        try {
            this.activity = activity;
            this.events = events;

            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        } catch (Exception e) {

        }
    }

//    public int getCount() {
//        return lPerson.size();
//    }
//
//    public Product getItem(Product position) {
//        return position;
//    }
//
//    public long getItemId(int position) {
//        return position;
//    }

    public static class ViewHolder {
        public TextView display_name;
        public TextView display_number;
        public TextView display_time;
        public ImageView display_image;

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;
        try {
            if (convertView == null) {
                vi = inflater.inflate(R.layout.list_event, null);
                holder = new ViewHolder();

                holder.display_name = (TextView) vi.findViewById(R.id.eventName);
                holder.display_number = (TextView) vi.findViewById(R.id.eventDate);
                holder.display_time = (TextView) vi.findViewById(R.id.eventTime);
                holder.display_image = (ImageView) vi.findViewById(R.id.categoryImage);



                vi.setTag(holder);
            } else {
                holder = (ViewHolder) vi.getTag();
            }

            final String category = events.get(position).getCategory().toLowerCase().trim();

            holder.display_name.setText(events.get(position).getName());
            holder.display_number.setText(events.get(position).getDate());
            holder.display_time.setText(events.get(position).getTime()+" on ");

            if (category.equals("athletics")) {
                holder.display_image.setImageResource(R.drawable.sport);
            } else if (category.equals("academics")) {
                holder.display_image.setImageResource(R.drawable.book);
            } else if (category.equals("social")) {
                holder.display_image.setImageResource(R.drawable.network);
            } else if (category.equals("night-life")) {
                holder.display_image.setImageResource(R.drawable.moon);
            } else if (category.equals("gaming")) {
                holder.display_image.setImageResource(R.drawable.game);
            } else if (category.equals("entertainment")) {
                holder.display_image.setImageResource(R.drawable.tele);
            } else if (category.equals("activism")) {
                holder.display_image.setImageResource(R.drawable.mega);
            } else if (category.equals("party")) {
                holder.display_image.setImageResource(R.drawable.balloon);
            } else if (category.equals("other")) {
                holder.display_image.setImageResource(R.drawable.rsz_ban );
            }


        } catch (Exception e) {


        }
        return vi;
    }

}