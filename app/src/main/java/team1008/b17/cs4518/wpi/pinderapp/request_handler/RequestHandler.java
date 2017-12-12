package team1008.b17.cs4518.wpi.pinderapp.request_handler;

import org.json.JSONObject;

/**
 * Created by calper on 12/12/17.
 */

public interface RequestHandler {
    public boolean send(JSONObject obj);
    public JSONObject recieve();
}
