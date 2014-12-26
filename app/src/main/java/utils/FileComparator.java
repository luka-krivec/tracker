package utils;

import java.io.File;
import java.util.Comparator;
import java.util.Date;

public class FileComparator implements Comparator<File> {

    @Override
    public int compare(File a, File b) {
        Date date1 = DateUtilities.getShortDate(a.getName());
        Date date2 = DateUtilities.getShortDate(b.getName());
        return date1.compareTo(date2);
    }
}
