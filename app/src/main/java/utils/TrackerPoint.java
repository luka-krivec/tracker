package utils;

import java.io.Serializable;

/**
 * Created by Luka on 11.4.2015.
 */
public class TrackerPoint implements Serializable {

    private double lat;
    private double lng;
    private long time;

    public TrackerPoint(double lat, double lng, long time) {
        this.lat = lat;
        this.lng = lng;
        this.time = time;
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

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
