package utils;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import gpslogger.CyclingRoute;
import com.google.android.gms.maps.model.LatLng;

import android.location.Location;
import android.net.ParseException;


public class GpxParser {

    private String gpxFilePath;
    public String LAST_ERROR;

    public GpxParser(String gpxFilePath) {
        this.gpxFilePath = gpxFilePath;
    }

    public ArrayList<Location> getArrayLocations()
    {
        ArrayList<Location> arrayLocations = new ArrayList<Location>();
        Document doc = getDocumentFromFile(gpxFilePath);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        DateFormat dateFormatLong = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

        if(doc == null)
            return arrayLocations;

        NodeList nList = doc.getElementsByTagName("trkpt");
        Location loc;

        try
        {
            for (int i = 0; i < nList.getLength(); i++) {
                Node node = nList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    loc = new Location("");
                    Element gpxPoint = (Element) node;

                    double lat = Double.parseDouble( gpxPoint.getAttribute("lat") );
                    double lon = Double.parseDouble( gpxPoint.getAttribute("lon") );
                    loc.setLatitude(lat);
                    loc.setLongitude(lon);

                    Node eleNode = gpxPoint.getElementsByTagName("ele").item(0);
                    if(eleNode != null)
                        loc.setAltitude(Double.parseDouble(eleNode.getTextContent()));

                    Node timeNode = gpxPoint.getElementsByTagName("time").item(0);
                    if(timeNode != null)
                    {
                        if(timeNode.getTextContent().length() == 20) {
                            Date time = dateFormat.parse(timeNode.getTextContent());
                            loc.setTime(time.getTime());
                        } else {
                            Date time = dateFormatLong.parse(timeNode.getTextContent());
                            loc.setTime(time.getTime());
                        }
                    }

                    arrayLocations.add(loc);
                }
            }
        }
        catch(NumberFormatException ex)
        {
            LAST_ERROR = ex.getMessage();
        }
        catch(ParseException ex)
        {
            LAST_ERROR = ex.getMessage();
        }
        catch(Exception ex)
        {
            LAST_ERROR = ex.getMessage();
        }

        return arrayLocations;
    }

    public LatLng[] getArrayLatLng()
    {
        ArrayList<LatLng> listLatLng = new ArrayList<LatLng>();
        Document doc = getDocumentFromFile(gpxFilePath);

        if(doc == null)
            return listLatLng.toArray(new LatLng[0]);

        NodeList nList = doc.getElementsByTagName("trkpt");
        LatLng currPoint;

        for (int i = 0; i < nList.getLength(); i++) {
            Node node = nList.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {

                Element gpxPoint = (Element) node;

                double lat = Double.parseDouble( gpxPoint.getAttribute("lat").replace(',', '.') );
                double lon = Double.parseDouble( gpxPoint.getAttribute("lon").replace(',', '.') );
                currPoint = new LatLng(lat, lon);
                listLatLng.add(currPoint);

            }
        }

        LatLng[] arrayLatLng = listLatLng.toArray(new LatLng[listLatLng.size()]);

        return arrayLatLng;
    }

    private Document getDocumentFromFile(String gpxFilePath)
    {
        Document doc = null;

        try
        {
            File gpxFile = new File(gpxFilePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            doc = dBuilder.parse(gpxFile);
            doc.getDocumentElement().normalize();
        }
        catch(IOException ex)
        {
            LAST_ERROR = ex.getMessage();
            ex.printStackTrace();
        }
        catch(SAXException ex)
        {
            LAST_ERROR = ex.getMessage();
            ex.printStackTrace();
        }
        catch(ParserConfigurationException ex)
        {
            LAST_ERROR = ex.getMessage();
            ex.printStackTrace();
        }

        return doc;
    }

    public CyclingRoute getCyclingRoute(ArrayList<Location> locations)
    {
        CyclingRoute cyclingRoute = new CyclingRoute();
        Location lastLocation = null;

        String routeName = new File(gpxFilePath).getName().replace(".gpx", "");
        cyclingRoute.setName(routeName);
		/*double maxSpeed = Double.MIN_VALUE;
		double speed;*/

        for(Location loc : locations)
        {
            if(lastLocation != null)
            {
                cyclingRoute.increaseDistance(loc.distanceTo(lastLocation));

                if(loc.getAltitude() > 0 && lastLocation.getAltitude() > 0 && (loc.getAltitude() - lastLocation.getAltitude()) > 0)
                    cyclingRoute.increaseAltitude(loc.getAltitude() - lastLocation.getAltitude());

				/*speed = lastLocation.distanceTo(loc) / (loc.getTime() / 1000 - lastLocation.getTime() / 1000);
				speed = speed * 3.6; // km/h

				if(speed > maxSpeed)
					maxSpeed = speed;*/
            }

            lastLocation = loc;
        }

        if(locations.size() > 0) {
            cyclingRoute.setStartTime(new Date(locations.get(0).getTime()));
            cyclingRoute.setEndTime(new Date(locations.get(locations.size()-1).getTime() ));
        }

        long timeDiff = (cyclingRoute.getEndTime().getTime()) - (cyclingRoute.getStartTime().getTime());
        cyclingRoute.setTime(timeDiff);

        cyclingRoute.setAverageSpeed(cyclingRoute.calculateAverageSpeed());
        //cyclingRoute.setMaxSpeed(maxSpeed);

        return cyclingRoute;
    }

}
