package uk.co.almanacmedia.dealchasr.dealchasr;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.content.Context;
import android.provider.Settings;
import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import java.net.URL;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import org.json.JSONObject;
import org.json.JSONException;


/**
 * Created by Sam on 09/02/2018.
 */

class DoRegisterFCM extends AsyncTask<Void, Void, String> {

    private Exception exception;
    private String API_URL = "http://api.almanacmedia.co.uk/notifications/register";
    private String authKey = "DS1k1Il68_uPPoD:3";
    private Integer uid;
    public  MainMapActivity context;
    public static final String PREFS_NAME = "DealSpotr.Data";
    public String nid;

    public DoRegisterFCM(MainMapActivity context, String nid, Integer uid){
        this.context = context;
        this.uid = uid;
        this.nid = nid;
    }

    protected void onPreExecute() {

    }

    protected String doInBackground(Void... urls) {

        try {

            final SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
            final String token = settings.getString("apitoken", PREFS_NAME);
            final String usertoken = settings.getString("usertoken", PREFS_NAME);
            final Integer userid = settings.getInt("userID", 0);

            String postParameters = "uid=" + uid + "&did=" + nid;

            URL url = new URL(API_URL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestProperty("Authorization", authKey);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            urlConnection.setRequestProperty("DSToken", token);
            urlConnection.setRequestProperty("DSUid", "" + userid);
            urlConnection.setRequestProperty("DSUtoken", usertoken);

            urlConnection.setFixedLengthStreamingMode(
                    postParameters.getBytes().length);

            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(postParameters);
            out.close();

            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                bufferedReader.close();
                return stringBuilder.toString();
            } finally {
                urlConnection.disconnect();
            }
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage(), e);
            Toast.makeText(context,
                    "Please check your internet connection and reopen the app.",
                    Toast.LENGTH_LONG).show();
            return null;
        }
    }

    protected void onPostExecute(String response) {
        if (response == null) {
            response = "THERE WAS AN ERROR";
        } else {
            try {
                JSONObject json = new JSONObject(response);
                parseDeviceJSON(json);
            } catch (JSONException e) {
                Log.e("JSON: ", e.getMessage(), e);
            }
        }
        Log.i("INFO", response);
    }

    private void parseDeviceJSON(JSONObject json){
        if(json != null){
            try {
                if(json.getBoolean("FCM")){
                    Log.i("FCM REGISTER", "Instance ID Registered Successfully.");
                    context.setSettings(json.getInt("global"), json.getInt("favourite"));
                } else {
                    Log.i("FCM REGISTER", json.getString("FCM"));
                }
            } catch (JSONException e) {
                Log.e("JSON Error", e.getMessage(), e);
            }
        } else {
            Log.i("Error", "Parsing device JSON");
        }
    }
}
