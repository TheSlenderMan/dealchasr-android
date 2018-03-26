package uk.co.almanacmedia.dealchasr.dealchasr;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by Sam on 24/03/2018.
 */

public class DoNullifyVoucher extends AsyncTask<Void, Void, String> {

    private Integer userID;
    private Integer voucherID;
    private Context context;
    private String API_URL = "http://api.almanacmedia.co.uk/vouchers/nullify";
    private String authKey = "DS1k1Il68_uPPoD:3";
    private ProgressDialog PD;
    public static final String PREFS_NAME = "DealSpotr.Data";

    public DoNullifyVoucher(Context context, Integer voucherID, Integer userID, ProgressDialog PD){
        this.context = context;
        this.userID = userID;
        this.voucherID = voucherID;
        this.PD = PD;
    }

    protected void onPreExecute(){
        this.PD.setMessage("Removing Voucher...");
        this.PD.setCancelable(false);
        this.PD.show();
    }

    protected String doInBackground(Void... urls){
        try {

            final SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
            final String token = settings.getString("apitoken", PREFS_NAME);
            final String usertoken = settings.getString("usertoken", PREFS_NAME);
            final Integer userid = settings.getInt("userID", 0);

            String postParameters = "userID=" + userID + "&voucherID=" + voucherID;

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

    protected void onPostExecute(String response){
        Log.i("JSON", response);
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
    }

    public void parseDeviceJSON(JSONObject json){
        if(json != null){
            this.PD.dismiss();
            Intent intent = new Intent(context, MainMapActivity.class);
            context.startActivity(intent);
            ((Activity)context).finish();
        } else {
            Log.i("Error", "Parsing device JSON");
        }
    }
}
