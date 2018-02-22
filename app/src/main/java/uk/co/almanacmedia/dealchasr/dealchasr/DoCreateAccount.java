package uk.co.almanacmedia.dealchasr.dealchasr;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import uk.co.almanacmedia.dealchasr.dealspotr.R;

/**
 * Created by Sam on 17/02/2018.
 */

public class DoCreateAccount extends AsyncTask<Void, Void, String> {
    private Exception exception;
    private String API_URL = "http://api.almanacmedia.co.uk/users/create";
    private String authKey = "DS1k1Il68_uPPoD";
    public Context context;
    public TextView error;
    public  String name;
    public  String email;
    public  String password;
    public static final String PREFS_NAME = "DealSpotr.Data";
    public ProgressDialog PD;

    public DoCreateAccount(Context context, TextView error, String name, String email, String password){
        this.context  = context;
        this.name     = name;
        this.email    = email;
        this.password = password;
        this.error = error;
    }

    protected void onPreExecute() {
        this.PD = new ProgressDialog(this.context, R.style.CustomDialog);
        this.PD.setMessage("Creating Account...");
        this.PD.setCancelable(false);
        this.PD.show();
    }

    protected String doInBackground(Void... urls) {

        try {

            String postParameters = "fullName=" + this.name + "&email=" + this.email.toLowerCase() + "&password=" + this.password;

            Log.i("INFO:", postParameters);

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
        this.PD.dismiss();
    }

    private void parseDeviceJSON(JSONObject json){
        if(json != null){
            try {
                JSONObject data = json.getJSONObject("data");
                if(data.getInt("created") == 1){
                    SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putInt("userID", data.getInt("userID"));
                    editor.apply();

                    Intent intent = new Intent(context, MainMapActivity.class);
                    context.startActivity(intent);
                    ((Activity)context).finish();
                } else {
                    error.setText("FAILED TO CREATE ACCOUNT. PLEASE TRY AGAIN");
                }
            } catch (JSONException e) {
                Log.e("JSON Error", e.getMessage(), e);
            }
        } else {
            Log.i("Error", "Parsing device JSON");
        }
    }
}
