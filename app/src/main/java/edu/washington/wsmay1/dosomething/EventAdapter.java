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
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Adapter to bind a ToDoItem List to a view
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

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;
        try {
            if (convertView == null) {
                vi = inflater.inflate(R.layout.list_event, null);
                holder = new ViewHolder();

                holder.display_name = (TextView) vi.findViewById(R.id.eventName);
                holder.display_number = (TextView) vi.findViewById(R.id.eventDescription);


                vi.setTag(holder);
            } else {
                holder = (ViewHolder) vi.getTag();
            }



            holder.display_name.setText(events.get(position).getName());
            holder.display_number.setText(events.get(position).getDescription());


        } catch (Exception e) {


        }
        return vi;
    }

}