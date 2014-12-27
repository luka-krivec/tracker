package utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class FileUtilities {

    public static String LAST_ERROR;

    public static void copyFile(File aSourceFile, File aTargetFile, boolean aAppend) {
        LAST_ERROR = "";
        InputStream inStream = null;
        OutputStream outStream = null;

        try {
            try {
                byte[] bucket = new byte[32 * 1024];
                inStream = new BufferedInputStream(new FileInputStream(aSourceFile));
                outStream = new BufferedOutputStream(new FileOutputStream(aTargetFile, aAppend));
                int bytesRead = 0;

                while (bytesRead != -1) {
                    bytesRead = inStream.read(bucket); // -1, 0, or more
                    if (bytesRead > 0) {
                        outStream.write(bucket, 0, bytesRead);
                    }
                }
            } finally {
                if (inStream != null)
                    inStream.close();
                if (outStream != null)
                    outStream.close();
            }
        } catch (FileNotFoundException ex) {
            LAST_ERROR = ex.getMessage();
        } catch (IOException ex) {
            LAST_ERROR = ex.getMessage();
        }
    }

}