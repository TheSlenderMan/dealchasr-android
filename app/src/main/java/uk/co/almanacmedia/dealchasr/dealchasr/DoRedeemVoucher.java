package uk.co.almanacmedia.dealchasr.dealchasr;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import uk.co.almanacmedia.dealchasr.dealchasr.R;

/**
 * Created by Sam on 21/02/2018.
 */

public class DoRedeemVoucher extends AsyncTask<Void, Void, String> {

    private Exception exception;
    private String API_URL = "http://api.almanacmedia.co.uk/vouchers/redeem";
    private String authKey = "DS1k1Il68_uPPoD";
    public  Context context;
    private Integer dealID;
    private Integer userID;
    private ProgressDialog PD;
    private PopupWindow pop;
    private Integer venueID;
    private PopupWindow donePop;
    private PopupWindow errorPop;
    private View v;
    private RecyclerAdapter.ViewHolder holder;
    public static final String PREFS_NAME = "DealSpotr.Data";

    public DoRedeemVoucher(Context context, Integer dealID, Integer userID, PopupWindow pop, View view, RecyclerAdapter.ViewHolder holder,
                           Integer venueID){
        this.context = context;
        this.dealID = dealID;
        this.userID = userID;
        this.pop = pop;
        this.v = view;
        this.holder = holder;
        this.venueID = venueID;
    }

    protected void onPreExecute(){
        this.PD = new ProgressDialog(this.context, R.style.CustomDialog);
        this.PD.setMessage("Getting your Voucher...");
        this.PD.setCancelable(false);
        this.PD.show();
    }

    protected String doInBackground(Void... urls){
        try {

            String postParameters = "userID=" + userID + "&voucherID=" + dealID;

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

    private void parseDeviceJSON(JSONObject json){
        if(json != null){
            try {

                JSONObject data = json.getJSONObject("data");

                pop.dismiss();

                if(data.getInt("created") == 1) {
                    Intent intent = new Intent(context, ViewVoucherActivity.class);
                    intent.putExtra("userID", (Integer) userID);
                    intent.putExtra("voucherID", (Integer) dealID);
                    context.startActivity(intent);
                    ((Activity)context).finish();
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
