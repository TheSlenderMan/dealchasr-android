package uk.co.almanacmedia.dealchasr.dealchasr;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import uk.co.almanacmedia.dealchasr.dealspotr.R;

/**
 * Created by Sam on 19/02/2018.
 */

public class DoGetVenue extends AsyncTask<Void, Void, String>{
    private Exception exception;
    private String API_URL = "http://api.almanacmedia.co.uk/venues/this";
    private String authKey = "DS1k1Il68_uPPoD";
    public Context context;
    public Integer VID;
    public ProgressDialog PD;
    public View v;
    private RecyclerView recyclerView;
    ArrayList<RecyclerModel> voucherList;

    public DoGetVenue(Context context, Integer VID, View v, RecyclerView recyclerView){
        this.context  = context;
        this.VID = VID;
        this.v = v;
        this.recyclerView = recyclerView;
    }

    protected void onPreExecute() {
        this.PD = new ProgressDialog(this.context, R.style.CustomDialog);
        this.PD.setMessage("Getting Deals...");
        this.PD.setCancelable(false);
        this.PD.show();

        voucherList = new ArrayList<RecyclerModel>();
    }

    protected String doInBackground(Void... urls) {

        try {

            SharedPreferences settings = context.getSharedPreferences(RecyclerAdapter.PREFS_NAME, 0);
            final Integer userID = settings.getInt("userID", 0);

            URL url = new URL(API_URL + "?VID=" + this.VID + "&userID=" + userID);
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
                if(data.getInt("found") == 1){
                    JSONObject venue = data.getJSONObject("venues");

                    Integer venueID   = venue.getInt("id");
                    String venueName  = venue.getString("vName");
                    String venueDesc  = venue.getString("vDescription");
                    String venueWeb   = venue.getString("vWebsite");
                    String addressOne = venue.getString("vAddressOne");
                    String addressTwo = venue.getString("vAddressTwo");
                    String cityTown   = venue.getString("vCityTown");
                    String county     = venue.getString("vCounty");
                    String country    = venue.getString("vCountry");
                    String postCode   = venue.getString("vPostCode");
                    String header     = venue.getString("vHeader");

                    JSONArray vouchers = venue.getJSONArray("vouchers");
                    JSONArray deals    = venue.getJSONArray("deals");

                    int count = vouchers.length();
                    int dcount = deals.length();
                    if(count == 0 && dcount == 0){
                        voucherList.add(new RecyclerModel("NO DEALS", "AVAILABLE", "Please check back regularly so you don't miss out!", "EMPTY", 0,
                                "0", 0, venueName, venueWeb, venueID, false, "0"));
                    } else {
                        if(count > 0){
                            for (int i = 0; i < count; i++) {
                                JSONObject jsonObject2 = vouchers.getJSONObject(i);
                                Integer voucherID = jsonObject2.getInt("id");
                                String voucherName = jsonObject2.getString("voucherName");
                                String dealName = jsonObject2.getString("dealName");
                                String dealDescription = jsonObject2.getString("voucherDescription");
                                String voucherTime = jsonObject2.getString("endDate");
                                String voucherCount = jsonObject2.getString("voucherCount");
                                JSONArray status = jsonObject2.getJSONArray("status");
                                Boolean sStatus;
                                if(status.length() > 0){
                                    sStatus = true;
                                } else {
                                    sStatus = false;
                                }
                                String type = "VOUCHER";
                                voucherList.add(new RecyclerModel(voucherName, dealName, dealDescription, type, voucherID, voucherTime, 0, venueName
                                , venueWeb, venueID, sStatus, voucherCount));
                            }
                        }
                        if(dcount > 0){
                            for (int i = 0; i < dcount; i++) {
                                JSONObject jsonObject2 = deals.getJSONObject(i);
                                Integer dealID = jsonObject2.getInt("id");
                                String voucherName = jsonObject2.getString("dealName");
                                String dealName = jsonObject2.getString("dealTitle");
                                String dealDescription = jsonObject2.getString("dealDescription");
                                String dealDate = jsonObject2.getString("dealDate");
                                Integer recurring = jsonObject2.getInt("recurring");
                                JSONArray status = jsonObject2.getJSONArray("status");
                                Boolean sStatus;
                                if(status.length() > 0){
                                    sStatus = true;
                                } else {
                                    sStatus = false;
                                }
                                String type = "DEAL";
                                voucherList.add(new RecyclerModel(voucherName, dealName, dealDescription, type, dealID, dealDate, recurring, venueName,
                                        venueWeb, venueID, sStatus, "0"));
                            }
                        }
                    }


                    ImageView headerView = v.findViewById(R.id.venueHeader);

                    new GetImageForView(headerView).execute(header);

                    TextView venueTitleView = v.findViewById(R.id.venueTitleView);
                    TextView venueDescView = v.findViewById(R.id.venueDescView);

                    venueTitleView.setText(venueName.toUpperCase());
                    venueDescView.setText(venueDesc.toUpperCase());

                    RecyclerAdapter recyclerAdapter = new RecyclerAdapter(context, voucherList);
                    recyclerView.setAdapter(recyclerAdapter);

                } else {

                }
            } catch (JSONException e) {
                Log.e("JSON Error", e.getMessage(), e);
            }
        } else {
            Log.i("Error", "Parsing device JSON");
        }
    }
}

class GetImageForView extends AsyncTask<String, Void, Bitmap>{
    ImageView bmImage;

    public GetImageForView(ImageView bmImage) {
        this.bmImage = bmImage;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    protected void onPostExecute(Bitmap result) {
        bmImage.setImageBitmap(result);
    }
}
