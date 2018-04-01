package uk.co.almanacmedia.dealchasr.dealchasr;

import android.*;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import uk.co.almanacmedia.dealchasr.dealchasr.R;

import static android.content.Context.LOCATION_SERVICE;
import static uk.co.almanacmedia.dealchasr.dealchasr.MainMapActivity.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;

/**
 * Created by Sam on 21/02/2018.
 */

public class DoActivateVoucher extends AsyncTask<Void, Void, String> {

    private Integer userID;
    private Integer voucherID;
    private Context context;
    private String redeemed;
    private Exception exception;
    private Button activate;
    private String activeDate;
    private String API_URL = "http://api.almanacmedia.co.uk/vouchers/use";
    private String authKey = "DS1k1Il68_uPPoD:3";
    public static final String PREFS_NAME = "DealSpotr.Data";

    public DoActivateVoucher(Context context, Integer userID, Integer voucherID, String redeemed, Button activate, String activeDate){
        this.context = context;
        this.userID = userID;
        this.voucherID = voucherID;
        this.redeemed = redeemed;
        this.activate = activate;
        this.activeDate = activeDate;
    }

    protected void onPreExecute(){

    }

    protected String doInBackground(Void... urls){
        try {

            String postParameters = "userID=" + userID + "&voucherID=" + voucherID + "&activeDate=" + URLEncoder.encode(activeDate, "utf-8");
            Log.i("AURL", postParameters);
            final SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
            final String token = settings.getString("apitoken", PREFS_NAME);
            final String usertoken = settings.getString("usertoken", PREFS_NAME);
            final Integer userid = settings.getInt("userID", 0);

            URL url = new URL(API_URL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestProperty("Authorization", authKey);
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
            try {

                if(json.getBoolean("used")){
                    activate.setText("ACTIVATED");
                    activate.setEnabled(false);
                }

            } catch (JSONException e) {
                Log.e("JSON Error", e.getMessage(), e);
            }
        } else {
            Log.i("Error", "Parsing device JSON");
        }
    }
}
