package edu.washington.wsmay1.dosomething;

/**
 * Created by Henry on 5/16/2015.
 */
public class Event {
    private String id;
    private String name;
    private String time;
    private String date;
    private String description;
    private String author;
    private String lat;
    private String lng;


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
        return lng;
    }

    public final void setLng(String newlng) {
        lng = newlng;
    }

    public final void setAuthor(String author) {
        this.author = author;
    }

    public final void setDate(String date) {
        this.date = date;
    }


}