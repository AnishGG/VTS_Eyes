package ssadteam5.vtsapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by anish on 18/10/17.
 */

public class UserData {
    SharedPreferences pref;
    Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;
    UserSessionManager session;
    private String mToken;
    // Sharedpref file name
    private static final String PREFER_NAME = "UserData";

    // User name (make variable public to access from outside)
    public static final String KEY_RESPONSE = "response";

    public static final String DATA_FETCHED = "data_fetched";

    public UserData(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = pref.edit();
//        editor.putBoolean(DATA_FETCHED, false);
//        editor.commit();
    }

    public void fetchData(){
        session = new UserSessionManager(_context);
        mToken = session.getUserDetails().get(UserSessionManager.KEY_TOKEN);
        HttpURLConnection conn;
        try {
            String response = "";
            URL url = new URL("http://eyedentifyapps.com:8080/api/auth/device/all/");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "*/*");
            conn.setRequestProperty("Authorization", "Bearer " + mToken);
            InputStream in = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(in);
            int inputStreamData = inputStreamReader.read();
            while (inputStreamData != -1) {
                char current = (char) inputStreamData;
                inputStreamData = inputStreamReader.read();
                response += current;
            }
            Log.d("resp", response);
            editor.putString(KEY_RESPONSE, response);
            editor.putBoolean(DATA_FETCHED, true);
            editor.commit();
        }catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    /**
     * Get stored session data
     * */
    public HashMap<String, String> getResponse(){

        //Use hashmap to store user credentials
        HashMap<String, String> response = new HashMap<String, String>();

        // user response
        response.put(KEY_RESPONSE, pref.getString(KEY_RESPONSE, null));

        return response;
    }

    public boolean isDataFetched(){
        return pref.getBoolean(DATA_FETCHED, false);
    }

    public void destroyResponse(){
        editor.clear();
        editor.commit();
    }

}
