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

    FirebaseRequestHandler(String address) {
        try {
            url = new URL(address);
            urlConnection = (HttpURLConnection) url.openConnection();
        } catch (Exception e) {

        } finally {
            urlConnection.disconnect();
        }
    }


    @Override
    public boolean send(JSONObject obj) {
        String str = obj.toString();
        try {
            urlConnection.getOutputStream().write(str.getBytes());
            urlConnection.getOutputStream().flush();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public JSONObject recieve() {
        try {
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            String result = IOUtils.toString(in, "UTF-8");
            JSONObject obj = new JSONObject(result);
            return  obj;
        } catch (Exception e) {
            return null;
        }

    }
}
