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
import java.net.HttpURLConnection;
import java.net.URL;
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

public class DoGetVoucher extends AsyncTask<Void, Void, String> {

    private Integer userID;
    private Integer voucherID;
    private Context context;
    private Exception exception;
    private String API_URL = "http://api.almanacmedia.co.uk/vouchers/view";
    private String authKey = "DS1k1Il68_uPPoD:3";
    private ProgressDialog PD;
    private View view;
    public static final String PREFS_NAME = "DealSpotr.Data";

    public DoGetVoucher(Context context, Integer userID, Integer voucherID, View view){
        this.context = context;
        this.userID = userID;
        this.voucherID = voucherID;
        this.view = view;
    }

    protected void onPreExecute(){
        this.PD = new ProgressDialog(this.context, R.style.CustomDialog);
        this.PD.setMessage("Loading Voucher...");
        this.PD.setCancelable(false);
        this.PD.show();
    }

    protected String doInBackground(Void... urls){
        try {

            final SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
            final String token = settings.getString("apitoken", PREFS_NAME);
            final String usertoken = settings.getString("usertoken", PREFS_NAME);
            final Integer userid = settings.getInt("userID", 0);

            URL url = new URL(API_URL + "?userID=" + userID + "&voucherID=" + voucherID);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestProperty("Authorization", authKey);
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("DSToken", token);
            urlConnection.setRequestProperty("DSUid", "" + userid);
            urlConnection.setRequestProperty("DSUtoken", usertoken);

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
        this.PD.dismiss();
    }

    public void parseDeviceJSON(JSONObject json){
        if(json != null){
            try {

                JSONObject data = json.getJSONObject("data");

                if(data.getInt("found") == 1) {
                    JSONObject v = data.getJSONObject("voucher");

                    String time = v.getString("endDate");
                    String voucherName = v.getString("voucherName");
                    String dealName = v.getString("dealName");
                    String venueName= v.getString("vName");
                    String venueWebsite = v.getString("vWebsite");
                    final Double vlat = v.getDouble("vlat");
                    final Double vlong = v.getDouble("vlong");
                    final String redeemed = v.getString("redeemed");
                    final Integer used = v.getInt("used");
                    final long difference = v.getLong("difference");

                    Log.d("DIFF: ", ":" + difference);

                    final CountDownTimer c;

                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
                        Date mDate = sdf.parse(time);
                        String nDate = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                        Date nowDate = sdf.parse(nDate);
                        long nowMilli = nowDate.getTime();
                        long timeInMilliseconds = mDate.getTime();
                        long diff = timeInMilliseconds - nowMilli;

                        final TextView timeTillEnd = view.findViewById(R.id.timeTillEnd);
                        final TextView voucherTitle= view.findViewById(R.id.voucherVDetails);
                        final ImageView codeView = view.findViewById(R.id.codeHolder);
                        final Button cmi = view.findViewById(R.id.cmi);
                        final Button activate = view.findViewById(R.id.activate);

                        String titleText = "" + voucherName + " " + dealName + " @ " + venueName;

                        voucherTitle.setText(titleText);

                        c = new CountDownTimer(diff, 1000) {
                            public void onTick(long millisUntilFinished) {
                            timeTillEnd.setText("VOUCHER EXPIRES IN: \n" + (TimeUnit.MILLISECONDS.toHours(millisUntilFinished) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(millisUntilFinished))) + ":"
                                        + (TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished))) + ":"
                                        + (TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))) + "");
                            }

                            public void onFinish() {

                            }
                        };
                        c.start();

                        try{
                            Bitmap bitmap = TextToImageEncode(venueWebsite);
                            codeView.setImageBitmap(bitmap);
                        } catch (WriterException e) {
                            e.printStackTrace();
                        }

                        cmi.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new DoNullifyVoucher(context, voucherID, userID, PD).execute();
                            }
                        });

                        if(used == 1){
                            c.cancel();
                            timeTillEnd.setText("VOUCHER ACTIVE\nGet your order in 15 minutes.");


                            new CountDownTimer((difference * 1000), 1000) {
                                public void onTick(long millisUntilFinished) {
                                    activate.setText("USE WITHIN: \n" + (TimeUnit.MILLISECONDS.toHours(millisUntilFinished) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(millisUntilFinished))) + ":"
                                            + (TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished))) + ":"
                                            + (TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))) + "");
                                }

                                public void onFinish() {
                                    Intent intent = new Intent(context, MainMapActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                                    context.startActivity(intent);
                                    ((Activity)context).finish();
                                }
                            }.start();
                        }

                        activate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
                                final String activeDate = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                                long dif = 0;
                                try {
                                    Date now = sdf2.parse(activeDate);
                                    final long n = now.getTime();
                                    final long nq = n + 900000;
                                    dif = nq - n;
                                } catch (ParseException e){
                                    e.printStackTrace();
                                }

                                LocationManager locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
                                Criteria criteria = new Criteria();
                                String provider = locationManager.getBestProvider(criteria, true);
                                try {
                                    Location location = locationManager.getLastKnownLocation(provider);
                                    if (location != null) {
                                        double latitude = location.getLatitude();
                                        double longitude = location.getLongitude();
                                        //LatLng latLng = new LatLng(latitude, longitude);
                                        //LatLng myPosition = new LatLng(latitude, longitude);

                                        /*float[] results = new float[1];
                                        Location.distanceBetween(vlat, vlong,
                                                myPosition.latitude, myPosition.longitude, results);*/

                                        Location selected_location = new Location("locationA");
                                        selected_location.setLatitude(latitude);
                                        selected_location.setLongitude(longitude);
                                        Location near_locations = new Location("locationB");
                                        near_locations.setLatitude(vlat);
                                        near_locations.setLongitude(vlong);
                                        double distance = selected_location.distanceTo(near_locations);

                                        Log.i("DISTANCE", "RES: " + distance);
                                        if(distance < 100){

                                            c.cancel();
                                            timeTillEnd.setText("VOUCHER ACTIVE\nGet your order in 15 minutes.");


                                            new CountDownTimer(dif, 1000) {
                                                public void onTick(long millisUntilFinished) {
                                                    activate.setText("USE WITHIN: \n" + (TimeUnit.MILLISECONDS.toHours(millisUntilFinished) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(millisUntilFinished))) + ":"
                                                            + (TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished))) + ":"
                                                            + (TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))) + "");
                                                }

                                                public void onFinish() {
                                                    Intent intent = new Intent(context, MainMapActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                                                    context.startActivity(intent);
                                                    ((Activity)context).finish();
                                                }
                                            }.start();

                                            new DoActivateVoucher(context, userID, voucherID, redeemed, activate, activeDate).execute();
                                        } else {
                                            Toast.makeText(context,
                                                    "It does not look like you are at the venue. Activate the voucher when you are there.",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    }
                                } catch (SecurityException e) {
                                    Toast.makeText(context,
                                            "Location permissions not accepted.",
                                            Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(context, MainMapActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                                    context.startActivity(intent);
                                    ((Activity)context).finish();
                                }
                            }
                        });

                    } catch (ParseException e){
                        e.printStackTrace();
                    }

                } else {

                }

            } catch (JSONException e) {
                Log.e("JSON Error", e.getMessage(), e);
            }
        } else {
            Log.i("Error", "Parsing device JSON");
        }
    }

    public Bitmap TextToImageEncode(String Value) throws WriterException {
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(
                    Value,
                    BarcodeFormat.DATA_MATRIX.QR_CODE,
                    150, 150, null
            );

        } catch (IllegalArgumentException Illegalargumentexception) {

            return null;
        }
        int bitMatrixWidth = bitMatrix.getWidth();

        int bitMatrixHeight = bitMatrix.getHeight();

        int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

        for (int y = 0; y < bitMatrixHeight; y++) {
            int offset = y * bitMatrixWidth;

            for (int x = 0; x < bitMatrixWidth; x++) {

                pixels[offset + x] = bitMatrix.get(x, y) ?
                        context.getResources().getColor(R.color.qrBlack):context.getResources().getColor(R.color.colorPrimary);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

        bitmap.setPixels(pixels, 0, 150, 0, 0, bitMatrixWidth, bitMatrixHeight);
        return bitmap;
    }
}
