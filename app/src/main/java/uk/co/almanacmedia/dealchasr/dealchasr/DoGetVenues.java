package uk.co.almanacmedia.dealchasr.dealchasr;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
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
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import uk.co.almanacmedia.dealchasr.dealspotr.R;

/**
 * Created by Sam on 17/02/2018.
 */

public class DoGetVenues extends AsyncTask<Void, Void, String> {
    private Exception exception;
    private String API_URL = "http://api.almanacmedia.co.uk/venues/all";
    private String authKey = "DS1k1Il68_uPPoD";
    public Context context;
    public GoogleMap map;
    public ProgressDialog PD;

    public DoGetVenues(GoogleMap map, Context context){
        this.context=context;
        this.map = map;
    }

    protected void onPreExecute() {
        this.PD = new ProgressDialog(this.context, R.style.CustomDialog);
        this.PD.setMessage("Deal Spotting...");
        this.PD.setCancelable(false);
        this.PD.show();
    }

    protected String doInBackground(Void... urls) {

        try {

            URL url = new URL(API_URL);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestProperty("Authorization", authKey);
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
                JSONObject data = json.getJSONObject("data");
                Integer found = data.getInt("found");
                if (found == 1) {
                    JSONArray array = data.getJSONArray("venues");

                    Integer height = 0;
                    Integer width = 0;
                    BitmapDrawable bitmapDraw;
                    Bitmap b;
                    Bitmap smallMarker;

                    for (Integer i = 0; i < array.length(); i++){
                        JSONObject row = array.getJSONObject(i);
                        Integer venueID   = row.getInt("id");
                        String venueName  = row.getString("vName");
                        String addressOne = row.getString("vAddressOne");
                        String addressTwo = row.getString("vAddressTwo");
                        String cityTown   = row.getString("vCityTown");
                        String county     = row.getString("vCounty");
                        String country    = row.getString("vCountry");
                        String postCode   = row.getString("vPostCode");
                        JSONArray vouchers= row.getJSONArray("vouchers");
                        JSONArray deals   = row.getJSONArray("deals");

                        Integer vcount = vouchers.length();
                        Integer dcount = deals.length();

                        String fullAddress = addressOne + ", " + cityTown + ", " + county + ", " + country + ", " + postCode;
                        Log.i("ADDRESS: ", fullAddress);
                        Geocoder coder = new Geocoder(context);
                        try{
                            List<Address> address = coder.getFromLocationName(fullAddress, 5);
                            Log.d("list", address.toString());
                            if(address != null && address.size() > 0){

                                Address location = address.get(0);
                                Marker thisMarker = this.map.addMarker(new MarkerOptions()
                                .position(new LatLng(location.getLatitude(), location.getLongitude())));
                                thisMarker.setTag(venueID);

                                if(vcount > 0 || dcount > 0){
                                    height = 150;
                                    width  = 145;
                                    bitmapDraw = (BitmapDrawable) context.getResources().getDrawable(R.drawable.mappin);
                                    b = bitmapDraw.getBitmap();
                                    smallMarker = Bitmap.createScaledBitmap(b, width, height, false);
                                    thisMarker.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                                } else {
                                    int height1 = 150;
                                    int width1  = 145;
                                    BitmapDrawable bitmapDraw1 = (BitmapDrawable) context.getResources().getDrawable(R.drawable.mappinnone);
                                    Bitmap b1 = bitmapDraw1.getBitmap();
                                    Bitmap smallMarker1 = Bitmap.createScaledBitmap(b1, width1, height1, false);
                                    thisMarker.setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker1));
                                }

                            } else {
                                new DoMapsDirect(context, map, fullAddress, venueID, vcount, dcount).execute();
                            }
                        } catch (IOException e){
                            Log.e("CODER Error", e.getMessage(), e);
                        }
                    }
                } else {
                    Log.e("VENUES", "NO VENUES AVAILABLE.");
                }
            } catch (JSONException e){
                Log.e("JSON Error", e.getMessage(), e);
            }
        } else {
            Log.e("JSON Error", "JSON IS NULL");
        }

        this.PD.dismiss();
    }
}
