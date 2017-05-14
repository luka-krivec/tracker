package utils;


import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WebUtils {

    public static final OkHttpClient client = new OkHttpClient();
    public static final MediaType URLENCODED = MediaType.parse("application/x-www-form-urlencoded");

    public static String executePost(String targetURL, String urlParameters) {
        String result = "";
        RequestBody body = RequestBody.create(URLENCODED, urlParameters);
        Request request = new Request.Builder()
                .url(targetURL)
                .post(body)
                .build();

        try(Response response = client.newCall(request).execute()) {
            result =  response.body().string();
            Log.d("WebUtils post response", result);
        } catch (IOException e) {
            FirebaseCrash.log("POST ["  + targetURL + "/" + urlParameters + "] failed! " +
                "Reason: " + e.getMessage());
            e.printStackTrace();
            Log.d("WebUtils post", e.getMessage());
        }

        return result;
    }

    @Deprecated
    public static String executePost2(String targetURL, String urlParameters) {
        URL url;
        HttpURLConnection connection = null;

        try {
            //Create connection
            url = new URL(targetURL);
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            connection.setRequestProperty("Content-Length", "" +
                    Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches (false);
            connection.setDoInput(true);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream (
                    connection.getOutputStream ());
            wr.writeBytes (urlParameters);
            wr.flush ();
            wr.close ();

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, "UTF8"));
            String line;
            StringBuffer response = new StringBuffer();
            while((line = rd.readLine()) != null) {
                response.append(line);
            }
            rd.close();
            Log.d("WebUtils post response", response.toString());
            return response.toString();

        } catch (Exception e) {

            e.printStackTrace();
            Log.d("WebUtils post", e.getMessage());
            return null;

        } finally {

            if(connection != null) {
                connection.disconnect();
            }
        }
    }
}
