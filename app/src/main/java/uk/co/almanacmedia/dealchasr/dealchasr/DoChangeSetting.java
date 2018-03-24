package uk.co.almanacmedia.dealchasr.dealchasr;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Sam on 24/03/2018.
 */

public class DoChangeSetting extends AsyncTask<Void, Void, String> {

    private Exception exception;
    private String API_URL = "http://api.almanacmedia.co.uk/notifications/settings";
    private String authKey = "DS1k1Il68_uPPoD";
    private Integer tog;
    public  Context context;
    public static final String PREFS_NAME = "DealSpotr.Data";
    public String type;
    public Integer uid;
    public ProgressDialog PD;

    public DoChangeSetting(Context context, String type, Integer tog, Integer uid){
        this.context = context;
        this.tog = tog;
        this.type = type;
        this.uid = uid;
    }

    protected void onPreExecute() {
        this.PD = new ProgressDialog(this.context, R.style.CustomDialog);
        this.PD.setMessage("Saving...");
        this.PD.setCancelable(false);
        this.PD.show();
    }

    protected String doInBackground(Void... urls) {

        try {

            String postParameters = "tog=" + tog + "&type=" + type + "&uid=" + uid;

            URL url = new URL(API_URL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestProperty("Authorization", authKey);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

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
                    Log.i("FCM UPDATED", "FCM setting updated.");
                } else {
                    Log.i("FCM UPDATE", "Update failed.");
                }
            } catch (JSONException e) {
                Log.e("JSON Error", e.getMessage(), e);
            }
        } else {
            Log.i("Error", "Parsing device JSON");
        }
        this.PD.dismiss();
    }
}
