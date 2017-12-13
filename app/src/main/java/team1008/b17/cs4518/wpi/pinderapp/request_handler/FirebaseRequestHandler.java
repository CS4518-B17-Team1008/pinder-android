package team1008.b17.cs4518.wpi.pinderapp.request_handler;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;


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
    public JSONObject send(JSONObject obj, String path) throws Exception {
        String str = obj.toString();
        System.out.println("----CONNECTION----");
        url = new URL(address + path);
        System.out.println(str);
        System.out.println(url);
        urlConnection = (HttpURLConnection) url.openConnection();
        System.out.println("----CONNECTED----");
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Content-Type", "application/json");
        System.out.println("----POST----");
        urlConnection.setDoOutput(true);
        urlConnection.setDoInput(true);
        urlConnection.setChunkedStreamingMode(0);
        System.out.println("----DO----");
        urlConnection.setConnectTimeout(5000);
        System.out.println("----SETTIMEOUT----");
        urlConnection.setReadTimeout(5000);
        System.out.println("----READTIMEOUT----");
        OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
        out.write(str.getBytes());
        System.out.println(str.getBytes().toString());
        System.out.println("----WRITE----");
        out.flush();
        System.out.println("----FLUSH----");
        String result = urlConnection.getResponseMessage();
        System.out.println(urlConnection.getResponseCode());
        System.out.println(result);
        System.out.println("----RESULT----");
        InputStream in = new BufferedInputStream((urlConnection.getInputStream()));
        byte[] contents = new byte[1024];
        int bytesRead = 0;
        String strFileContents ="";
        while((bytesRead = in.read(contents)) != -1) {
            strFileContents += new String(contents, 0, bytesRead);
        }
        System.out.println(strFileContents);
        JSONObject res = new JSONObject(strFileContents);
        System.out.println("----JSON----");
        urlConnection.disconnect();
        System.out.println("----DISCONNECTED----");
        return res;
    }

}
