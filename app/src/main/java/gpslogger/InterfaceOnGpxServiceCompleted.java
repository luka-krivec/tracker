package gpslogger;

import java.util.ArrayList;
import java.util.Date;

import android.location.Location;

public interface InterfaceOnGpxServiceCompleted {
    void onServiceCompleted(ArrayList<Location> locations, String fileName, long timeDiff, Date dateStart, Date dateEnd);
}
