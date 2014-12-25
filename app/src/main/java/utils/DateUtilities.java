package utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateUtilities {

    public static String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String formatDate(Date date)
    {
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("GMT+2"));
        return df.format(date);
    }

    public static String formatTime(Date date)
    {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        return df.format(date);
    }

    public static String formatShortDate(Date date)
    {
        SimpleDateFormat df = new SimpleDateFormat("d.M.yyyy");
        return df.format(date);
    }

    public static String formatShortDate2(Date date)
    {
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        return df.format(date);
    }

    public static String formatDate(Date date, String format)
    {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }

    public static Date getDate(String date)
    {
        try
        {
            SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            df.setTimeZone(TimeZone.getTimeZone("GMT+2"));
            return df.parse(date);
        }
        catch(ParseException ex)
        {
            return null;
        }
    }

    public static Date getTime(String date)
    {
        try
        {
            SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
            return df.parse(date);
        }
        catch(ParseException ex)
        {
            return null;
        }
    }

    public static Date getShortDate(String date)
    {
        try
        {
            SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
            return df.parse(date);
        }
        catch(ParseException ex)
        {
            return null;
        }
    }

    public static String timeToString(long time) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.clear();
        //Use Gregorian calendar for all values
        calendar.setGregorianChange(new Date(Long.MIN_VALUE));

        calendar.setTimeZone( TimeZone.getTimeZone("UTC"));
        calendar.setTime(new Date(time));

        SimpleDateFormat fmt = new SimpleDateFormat("HH:mm:ss");
        fmt.setCalendar(calendar);
        String dateFormatted = fmt.format(calendar.getTime());
        return dateFormatted;
    }

    public static String secondsTo_hhmmss(long elapsed) {
        long hours = elapsed / 3600;
        long remainder = elapsed % 3600;
        long minutes = remainder / 60;
        long seconds = remainder % 60;

        return ( (hours < 10 ? "0" : "") + hours
                + ":" + (minutes < 10 ? "0" : "") + minutes
                + ":" + (seconds< 10 ? "0" : "") + seconds );
    }

    public static long timeStringToMillis(String time) {
        long timeMillis = 0;
        DateFormat dfDate = new SimpleDateFormat("HH:mm:ss");
        dfDate.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            Date dat = dfDate.parse(time);
            timeMillis = dat.getTime() ;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return timeMillis;
    }

    public static Date convertToPhoneTimeZone(Date date) {
    	/*try {
    		String dateString = formatDate(date);
        	SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        	df.setTimeZone(TimeZone.getDefault());
        	return df.parse(dateString);
    	} catch(ParseException ex) {
    		Log.e("parseException", ex.getMessage());
			return date;
    	}*/
        long time = date.getTime();
        time += (2 * 60 * 60 * 1000);
        return new Date(time); // UTC + 2

    }


}
