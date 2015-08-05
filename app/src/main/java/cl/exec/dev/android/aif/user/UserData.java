package cl.exec.dev.android.aif.user;

import java.util.Date;

/**
 * Created by fran on 26-07-15.
 */
public class UserData {

    String name;
    String email;
    double lat;
    double lng;
    Date date;

    public UserData(String name, String email, double lat, double lng, Date date) {
        this.name = name;
        this.email = email;
        this.lat = lat;
        this.lng = lng;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
