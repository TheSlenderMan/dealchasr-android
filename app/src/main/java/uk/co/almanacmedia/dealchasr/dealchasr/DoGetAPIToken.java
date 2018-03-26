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

class DoGetAPIToken extends AsyncTask<Void, Void, String> {

    private Exception exception;
    private String API_URL = "http://api.almanacmedia.co.uk/tokens/api";
    private String authKey = "DS1k1Il68_uPPoD:3";
    private String uuid;
    public  Context context;
    public static final String PREFS_NAME = "DealSpotr.Data";

    public DoGetAPIToken(Context context){
        this.context=context;
    }

    protected void onPreExecute() {

    }

    protected String doInBackground(Void... urls) {

        try {
            URL url = new URL(API_URL + "?client=3");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestProperty("Authorization", authKey);
            urlConnection.setRequestProperty("DSToken", "GAIN");
            urlConnection.setRequestProperty("DSUid", "LOGIN");
            urlConnection.setRequestProperty("DSUToken", "TOKEN");
            urlConnection.setRequestMethod("GET");

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
                String token = json.getString("token");
                SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("apitoken", token);
                editor.apply();

                new DoDeviceTouch(context).execute();
            } catch (JSONException e) {
                Log.e("JSON Error", e.getMessage(), e);
            }
        } else {
            Log.i("Error", "Parsing device JSON");
        }
    }
}
