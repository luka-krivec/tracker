package gpslogger;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import utils.DateUtilities;

public class CyclingRoute implements Serializable {

    private static final long serialVersionUID = 1L;

    private final DateFormat dfDate = new SimpleDateFormat("HH:mm:ss");

    // spremenljivke
    private double distance; // metri

    private double altitude; // višina (m)
    private double maxSpeed; // m/s
    private double averageSpeed; // km/h
    private long time; // milisekunde
    private String name; // poimenovanje poti
    private Date startTime;
    private Date endTime;
    private boolean first; // označimo za prikaz vseh aktivnosti


    // konstruktorji
    public CyclingRoute() {
        this.distance = 0;
        this.altitude = 0;
        this.maxSpeed = 0;
        this.name = "";
    }

    public CyclingRoute(double distance, long time) {
        this.distance = distance;
        this.time = time;
        this.altitude = 0;
    }

    public CyclingRoute(double distance, double altitude, long time) {
        this.distance = distance;
        this.altitude = altitude;
        this.time = time;
    }

    // time
    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        //this.startTime = DateUtilities.convertToPhoneTimeZone(startTime);
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        //this.endTime = DateUtilities.convertToPhoneTimeZone(endTime);
        this.endTime = endTime;
    }

    // distance
    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void increaseDistance(double meters) {
        this.distance += meters;
    }

    // average speed
    public double getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(double averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    // average speed (km/h)
    public double calculateAverageSpeed() {
        if(this.distance == 0 || this.time == 0)
            return 0;
        double ms = this.distance / (this.time / 1000); // m/s
        double kmh = ms * 3.6; // km/h
        return kmh;
    }

    //name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // first
    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    // altitude
    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public void increaseAltitude(double altitude) {
        this.altitude += altitude;
    }

    // speed
    public double getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(double maxSpeed) {
        this.maxSpeed = maxSpeed;
    }


    @Override
    public String toString() {
        String res = "Ime: " + this.name + "\n";
        res += "Razdalja: " + String.format("%.2f", this.distance) + "\n";
        res += "Čas: " + DateUtilities.timeToString(this.time) + "\n";
        res += "Čas začetka: " + DateUtilities.formatDate(startTime)  + "\n";
        res += "Čas konca: " + DateUtilities.formatDate(endTime) + "\n";
        res += "Povprečna hitrost: " + String.format("%.1f", getAverageSpeed()) + "\n";
        res += "Najvišja hitrost: " + String.format("%.1f", this.maxSpeed) + "\n";
        res += "Višina: " + String.format("%.0f", this.altitude);
        return res;
    }

}
