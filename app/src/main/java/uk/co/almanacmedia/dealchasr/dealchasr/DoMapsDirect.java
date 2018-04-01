package uk.co.almanacmedia.dealchasr.dealchasr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import uk.co.almanacmedia.dealchasr.dealchasr.R;

/**
 * Created by Sam on 18/02/2018.
 */

public class DoMapsDirect extends AsyncTask<Void, Void, String> {

    private Context context;
    private GoogleMap map;
    private String API_URL;
    private Integer VID;
    private Integer vcount;
    private Integer dcount;
    private Integer tier;
    private String name;

    public DoMapsDirect(Context context, GoogleMap map, String address, Integer VID, Integer vcount, Integer dcount, Integer tier, String name){
        this.context = context;
        this.map = map;
        this.VID = VID;
        this.vcount = vcount;
        this.dcount = dcount;
        this.tier = tier;
        this.name = name;
        try {
            this.API_URL = "https://maps.google.com/maps/api/geocode/json?address="
                    + URLEncoder.encode(address, "UTF-8") + "&ka&sensor=false&key=AIzaSyAD_QULIBE7yuoBpqKDk2lKCc_d_ye-llY";
        } catch (UnsupportedEncodingException e){
            Log.i("Error: ", e.getMessage());
        }
    }

    protected void onPreExecute() {

    }

    protected String doInBackground (Void... urls){
        try {

            URL url = new URL(API_URL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

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
        try {
            double lng = ((JSONArray) json.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lng");

            double lat = ((JSONArray) json.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lat");

            LatLng ll = new LatLng(lat, lng);

            Marker thisMarker = this.map.addMarker(new MarkerOptions()
                    .position(new LatLng(lat, lng)));
            thisMarker.setTag(VID);
            thisMarker.setTitle(this.name);
            String snip = "GO >";
            thisMarker.setSnippet(snip);

            BitmapDrawable bitmapDraw;
            Bitmap b;
            Bitmap smallMarker;

            if((vcount > 0 || dcount > 0) && tier == 3){
                int height = 150;
                int width  = 145;
                bitmapDraw = (BitmapDrawable) context.getResources().getDrawable(R.drawable.mappin);
                b = bitmapDraw.getBitmap();
                smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                thisMarker.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));
            }
            if(vcount < 1 && dcount < 1 && tier == 3){
                int height1 = 150;
                int width1  = 145;
                BitmapDrawable bitmapDraw1 = (BitmapDrawable) context.getResources().getDrawable(R.drawable.mappinnone);
                Bitmap b1 = bitmapDraw1.getBitmap();
                Bitmap smallMarker1 = Bitmap.createScaledBitmap(b1, width1, height1, false);
                thisMarker.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker1));
            }
            if((vcount > 0 || dcount > 0) && tier == 2){
                int height = 150;
                int width  = 145;
                bitmapDraw = (BitmapDrawable) context.getResources().getDrawable(R.drawable.mappin);
                b = bitmapDraw.getBitmap();
                smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                thisMarker.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));
            }

        } catch (JSONException e) {
            Log.i("Error: ", e.getMessage());
        }
    }
}
