package gpslogger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.location.Location;
import android.os.Environment;

public class GpxWriter {

    private static final String GPX_TRANSORM_ERROR = "Napaka pri transformaciji XML-ja.";
    private static final int TWO_HOURS = 1000 * 60 * 120;
    public static String GPX_ERROR;
    public static String GPX_DATA_ERROR;

    public static CyclingRoute cyclingRoute;

    /**
     * Iz seznama lokacij ustvari GPX datoteko.
     * @param locations Lokacije iz Android naprave
     * @param docName ime GPX poti
     * @param docDesc opis GPX poti
     * @return GPX dokument iz lokacij
     */
    public static Document CreateGpxFile(ArrayList<Location> locations, long timeDiff, Date dateStart, Date dateEnd, String docName, String docDesc) {
        Document doc = null;
        cyclingRoute = new CyclingRoute();
        cyclingRoute.setName(docName.replace(".gpx", ""));
        double maxSpeed = Double.MIN_VALUE;

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root element - gpx
            // <gpx xmlns="http://www.topografix.com/GPX/1/1" creator="kolesar-android" version="1.1"
            //    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            //    xsi:schemaLocation="http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd">
            doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("gpx");
            doc.appendChild(rootElement);

            Attr attrXmlns = doc.createAttribute("xmlns");
            attrXmlns.setValue("http://www.topografix.com/GPX/1/1");
            rootElement.setAttributeNode(attrXmlns);

            Attr attrCreator = doc.createAttribute("creator");
            attrCreator.setValue("kolesar-android");
            rootElement.setAttributeNode(attrCreator);

            Attr attrXmlnsxsi = doc.createAttribute("xmlns:xsi");
            attrXmlnsxsi.setValue("http://www.w3.org/2001/XMLSchema-instance");
            rootElement.setAttributeNode(attrXmlnsxsi);

            Attr attrXsischemaLocation = doc.createAttribute("xsi:schemaLocation");
            attrXsischemaLocation.setValue("http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd");
            rootElement.setAttributeNode(attrXsischemaLocation);


            // trk element
            Element trk = doc.createElement("trk");
            rootElement.appendChild(trk);

            // name element
            Element name = doc.createElement("name");
            name.appendChild(doc.createTextNode(docName));
            trk.appendChild(name);

            // desc elements
            Element desc = doc.createElement("desc");
            desc.appendChild(doc.createTextNode(docDesc));
            trk.appendChild(desc);

            // trkseg element
            Element trkseg = doc.createElement("trkseg");
            trk.appendChild(trkseg);


            // trkpt elements
            Element trkpt, ele, time;
            Date trkptDate;
            DateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            Location lastLocation = null;

            for(Location loc : locations) {
                trkpt = doc.createElement("trkpt");
                trkpt.setAttribute("lat", String.format("%.7f", loc.getLatitude()));
                trkpt.setAttribute("lon", String.format("%.7f", loc.getLongitude()));
                trkseg.appendChild(trkpt);

                ele = doc.createElement("ele");
                ele.appendChild(doc.createTextNode(String.format("%.1f", loc.getAltitude())));
                trkpt.appendChild(ele);

                time = doc.createElement("time");
                trkptDate = new Date(loc.getTime()); // pretvorba UTC v UTC+2
                time.appendChild(doc.createTextNode(dfDate.format(trkptDate)));
                trkpt.appendChild(time);

                // Izračunamo statistične podatke
                // ...

                // Povečamo razdaljo za oddaljenost med zadnjima dvema točkama
                if(lastLocation != null)
                {
                    cyclingRoute.increaseDistance(loc.distanceTo(lastLocation));
                    if(loc.hasAltitude() && lastLocation.hasAltitude() && (loc.getAltitude() - lastLocation.getAltitude()) > 0) // če smo se vzpeli prištejemo vzpon
                        cyclingRoute.increaseAltitude(loc.getAltitude() - lastLocation.getAltitude());
                }

                lastLocation = loc;

                // Shranimo najvišjo hitrost
                if(loc.hasSpeed() && (loc.getSpeed() > maxSpeed))
                    maxSpeed = loc.getSpeed();
            }

            // Čas vožnje
            cyclingRoute.setStartTime(dateStart);
            cyclingRoute.setEndTime(dateEnd);
            cyclingRoute.setTime(timeDiff);
            cyclingRoute.setAverageSpeed(cyclingRoute.calculateAverageSpeed());

            cyclingRoute.setMaxSpeed(maxSpeed);

            // ...
        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        }

        return doc;
    }

    public static String getStringFromDoc(Document doc) {
        String xmlString;

        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            xmlString = writer.getBuffer().toString().replaceAll("\n|\r", "");
        } catch(TransformerException ex) {
            return GPX_TRANSORM_ERROR;
        }

        return xmlString;
    }

    public static boolean canWriteToExternalStorage() {
        String state = Environment.getExternalStorageState();

        // Can we read and write the media
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static boolean writeGpxToExternalStorage(Document doc, File gpxLocation) {

        if(!canWriteToExternalStorage()) { // We cant write to external storage
            GPX_ERROR = "Ni pravic za pisanje na zunanji pomnilnik!";
            return false;
        }

        String gpxContent = getStringFromDoc(doc);

        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(gpxLocation));
            out.write(gpxContent);
            out.close();
        }
        catch (IOException e)
        {
            GPX_ERROR = e.getMessage();
            return false;
        }

        return true;
    }

    public static boolean saveCyclingRouteData(File savedGpxData, CyclingRoute cyclingRoute) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(savedGpxData));
            out.write(cyclingRoute.toString());
            out.close();
        }
        catch (Exception e)
        {
            GPX_DATA_ERROR = e.getMessage();
            return false;
        }

        return true;
    }

}
