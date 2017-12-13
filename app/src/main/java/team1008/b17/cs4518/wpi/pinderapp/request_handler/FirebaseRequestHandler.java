package team1008.b17.cs4518.wpi.pinderapp.request_handler;

import org.json.JSONObject;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;

/**
 * Created by calper on 12/12/17.
 */

public class FirebaseRequestHandler implements RequestHandler {
    private URL url;
    private HttpURLConnection urlConnection;
    String address;

    public FirebaseRequestHandler(String address) {
        this.address = address;
    }


    @Override
    public JSONObject send(JSONObject obj, String path) {
        String str = obj.toString();
        try {
            url = new URL(address+"/"+path);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.getOutputStream().write(str.getBytes());
            urlConnection.getOutputStream().flush();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            String result = IOUtils.toString(in, "UTF-8");
            JSONObject res = new JSONObject(result);
            urlConnection.disconnect();
            return  res;
        } catch (Exception e) {
            return null;
        }
    }

}
