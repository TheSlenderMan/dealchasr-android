package uk.co.almanacmedia.dealchasr.dealchasr;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.content.Context;
import android.app.Activity;
import android.content.Intent;
import android.widget.TextView;

import java.net.URL;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import org.json.JSONObject;
import org.json.JSONException;

import uk.co.almanacmedia.dealchasr.dealchasr.R;


/**
 * Created by Sam on 09/02/2018.
 */

class DoLogin extends AsyncTask<Void, Void, String> {

    private Exception exception;
    private String API_URL = "http://api.almanacmedia.co.uk/users/login";
    private String authKey = "DS1k1Il68_uPPoD";
    public  Context context;
    public  TextView error;
    public  String email;
    public  String password;
    public static final String PREFS_NAME = "DealSpotr.Data";
    public ProgressDialog PD;

    public DoLogin(Context context, TextView error, String email, String password){
        this.context  = context;
        this.email    = email;
        this.password = password;
        this.error = error;
    }

    protected void onPreExecute() {
        this.PD = new ProgressDialog(this.context, R.style.CustomDialog);
        this.PD.setMessage("Logging in...");
        this.PD.setCancelable(false);
        this.PD.show();
    }

    protected String doInBackground(Void... urls) {

        try {

            String postParameters = "email=" + this.email.toLowerCase() + "&password=" + this.password;

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
                if(json.getInt("loggedIn") == 1){
                    SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putInt("userID", json.getInt("userID"));
                    editor.apply();

                    Intent intent = new Intent(context, MainMapActivity.class);
                    context.startActivity(intent);
                    ((Activity)context).finish();
                } else {
                    error.setText(json.getString("message"));
                }
            } catch (JSONException e) {
                Log.e("JSON Error", e.getMessage(), e);
            }
        } else {
            Log.i("Error", "Parsing device JSON");
        }
    }
}
