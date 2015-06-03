package edu.washington.wsmay1.dosomething;

/**
 * Created by Henry on 5/16/2015.
 */
public class Event {
    private String id;
    private String name;
    private String category;
    private String time;
    private String date;
    private String description;
    private String author;
    private String lat;
    private String lng;
    private String host;

    public String getHost() {
        if (this.host != null) {
            return this.host;
        } else {
            return this.author;
        }

    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getId() {
        return id;
    }
    public final void setId(String newid) {
        id = newid;
    }

    public String getCategory() {
        return category;
    }
    public final void setCategory(String newCategory) {category = newCategory; }

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
        return this.date;
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
    public String getAuthor() { return author; }

    public final void setDate(String date) {
        this.date = date;
    }


}