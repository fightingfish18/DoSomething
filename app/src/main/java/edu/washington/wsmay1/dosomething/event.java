package edu.washington.wsmay1.dosomething;

/**
 * Created by Henry on 5/16/2015.
 */
public class Event {
    public String id;
    public String name;
    public String time;
    public String date;
    public String description;
    public String author;
    public String lat;
    public String lng;


    public String getId() {
        return id;
    }
    public final void setId(String newid) {
        id = newid;
    }

    public String getName() {
        return name;
    }
    public final void setName(String newname) {
        name = newname;
    }

    public String getTime() {
        return time;
    }
    public final void setTime(String newtime) {
        time = newtime;
    }

    public String getDate() {
        return id;
    }
    public final void setdate(String newdate) {
        date = newdate;
    }

    public String getDescription() {
        return description;
    }
    public final void setDescription(String newdescription) {
        description = newdescription;
    }

    public String getLat() {
        return lat;
    }
    public final void setLat(String newlat) {
        lat = newlat;
    }

    public String getLng() {
        return id;
    }
    public final void setLng(String newlng) {
        lng = newlng;
    }

}