package uk.co.almanacmedia.dealchasr.dealchasr;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
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
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import uk.co.almanacmedia.dealchasr.dealchasr.R;

/**
 * Created by Sam on 23/02/2018.
 */

public class DoGetMyInterests extends AsyncTask<Void,Void,String> {

    private Exception exception;
    private String API_URL = "http://api.almanacmedia.co.uk/users/deals";
    private String authKey = "DS1k1Il68_uPPoD:3";
    public Context context;
    public Integer UID;
    public ProgressDialog PD;
    public View v;
    private RecyclerView recyclerView;
    ArrayList<InterestsRecyclerModel> voucherList;
    public static final String PREFS_NAME = "DealSpotr.Data";

    public DoGetMyInterests(Context context, Integer userID, View v, RecyclerView recyclerView){
        this.context  = context;
        this.UID = userID;
        this.v = v;
        this.recyclerView = recyclerView;
    }

    protected void onPreExecute(){
        this.PD = new ProgressDialog(this.context, R.style.CustomDialog);
        this.PD.setMessage("Getting Your Vouchers...");
        this.PD.setCancelable(false);
        this.PD.show();

        voucherList = new ArrayList<InterestsRecyclerModel>();
    }

    protected String doInBackground(Void... urls){
        try {

            final SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
            final String token = settings.getString("apitoken", PREFS_NAME);
            final String usertoken = settings.getString("usertoken", PREFS_NAME);
            final Integer userid = settings.getInt("userID", 0);

            URL url = new URL(API_URL + "?userID=" + this.UID);
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
            return null;
        }
    }

    protected void onPostExecute(String response){
        if (response == null) {
            response = "THERE WAS AN ERROR";
        } else {
            try {
                JSONArray json = new JSONArray(response);
                parseDeviceJSON(json);
            } catch (JSONException e) {
                Log.e("JSON: ", e.getMessage(), e);
            }
        }
        Log.i("INFO", response);
        this.PD.dismiss();
    }

    public void parseDeviceJSON(JSONArray json){
        if(json != null){
            try {

                Integer count = json.length();
                for (int i = 0; i < count; i++) {
                    JSONObject jo = json.getJSONObject(i);
                    Integer dealID = jo.getInt("id");
                    String voucherName = jo.getString("dealName");
                    String dealName = jo.getString("dealTitle");
                    String dealDescription = jo.getString("dealDescription");
                    String dealDate = jo.getString("dealDate");
                    Integer recurring = jo.getInt("recurring");
                    String venueName = jo.getString("vName");
                    Integer daily = jo.getInt("daily");

                    voucherList.add(new InterestsRecyclerModel(dealID, voucherName, dealName, dealDescription, venueName, dealDate, recurring, daily));
                }


                InterestsRecyclerAdapter recyclerAdapter = new InterestsRecyclerAdapter(context, voucherList);
                recyclerView.setAdapter(recyclerAdapter);

            } catch (JSONException e) {
                Log.e("JSON Error", e.getMessage(), e);
            }
        } else {
            Log.i("Error", "Parsing device JSON");
        }
    }
}
