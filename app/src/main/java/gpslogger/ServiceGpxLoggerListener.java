package gpslogger;

import android.location.Location;
import android.util.Log;

import org.w3c.dom.Document;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import utils.DateUtilities;

public class ServiceGpxLoggerListener implements InterfaceOnGpxServiceCompleted, Serializable {

    private static final long serialVersionUID = 5181127533924120625L;
    private File dirExternalStorageGpxStore;
    public boolean saveStatus;

    public ServiceGpxLoggerListener(File dirExternalStorageGpxStore) {
        this.dirExternalStorageGpxStore = dirExternalStorageGpxStore;
    }

    @Override
    public void onServiceCompleted(ArrayList<Location> locations, String fileName, long timeDiff, Date dateStart, Date dateEnd) {

        String currentDate =  DateUtilities.getCurrentDate();

        // Ustvarimo mapo s trenutnim datumom, ce se ne obstaja
        File savedDir = new File(dirExternalStorageGpxStore, currentDate);
        savedDir.mkdirs();

        // Shranimo gpx datoteko v prej narejeno mapo
        File savedGpx = new File(savedDir, fileName + ".gpx");
        Document gpxDoc = GpxWriter.CreateGpxFile(locations, timeDiff, dateStart, dateEnd, fileName + ".gpx", "");
        boolean gpxSaveSuccess = GpxWriter.writeGpxToExternalStorage(gpxDoc, savedGpx);

        // Shranimo izračunane podatke o prevoženi poti v datoteko
        File savedGpxData = new File(savedDir, fileName + ".dat");
        boolean gpxDataSaveSuccess = GpxWriter.saveCyclingRouteData(savedGpxData, GpxWriter.cyclingRoute);

        if(gpxSaveSuccess)
            Log.d("Service completed", "Datoteka gpx/" + currentDate + "/" + fileName + ".gpx je shranjena.");
        else
            Log.d("Service completed", "NAPAKA !!! " + GpxWriter.GPX_ERROR);

        if(gpxDataSaveSuccess)
            Log.d("Service completed", "Datoteka gpx/" + currentDate + "/" + fileName + ".dat je shranjena.");
        else
            Log.d("Service completed", "NAPAKA !!! " + GpxWriter.GPX_DATA_ERROR);

        saveStatus = gpxSaveSuccess && gpxDataSaveSuccess;
    }

}
