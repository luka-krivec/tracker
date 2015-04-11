package utils;

import java.io.Serializable;

/**
 * Created by Luka on 11.4.2015.
 */
public class TrackerPoint implements Serializable {

    private double lat;
    private double  lng;

    public TrackerPoint(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
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
}
