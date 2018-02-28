package uk.co.almanacmedia.dealchasr.dealchasr;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import uk.co.almanacmedia.dealchasr.dealchasr.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * Created by Sam on 21/02/2018.
 */

public class DoMarkInterested extends AsyncTask<Void, Void, String> {

    private Exception exception;
    private String API_URL = "http://api.almanacmedia.co.uk/deals/interested";
    private String authKey = "DS1k1Il68_uPPoD";
    public  Context context;
    private Integer dealID;
    private Integer userID;
    private ProgressDialog PD;
    private PopupWindow pop;
    private PopupWindow donePop;
    private PopupWindow errorPop;
    private View v;
    private RecyclerAdapter.ViewHolder holder;
    public static final String PREFS_NAME = "DealSpotr.Data";

    public DoMarkInterested(Context context, Integer dealID, Integer userID, PopupWindow pop, View view, RecyclerAdapter.ViewHolder holder){
        this.context = context;
        this.dealID = dealID;
        this.userID = userID;
        this.pop = pop;
        this.v = view;
        this.holder = holder;
    }

    protected void onPreExecute(){
        this.PD = new ProgressDialog(this.context, R.style.CustomDialog);
        this.PD.setMessage("Please Wait...");
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

                ConstraintLayout mRelativeLayout = (ConstraintLayout) v.findViewById(R.id.main_venue);

                LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                View redeemPopover = inflater.inflate(R.layout.interested_done_popover, null);

                TextView doneText = redeemPopover.findViewById(R.id.intDoneText);

                holder.redeemButton.setText("YOU'RE GOING!");
                holder.redeemButton.setEnabled(false);

                String popText;
                if(data.getInt("created") == 1) {
                    popText = "SUCCESS!\n\nYOUR INTEREST HAS BEEN NOTED.";
                } else {
                    popText = "WHOOPS! THERE SEEMS TO BE\nA PROBLEM!\n\nPLEASE TRY AGAIN.";
                }

                doneText.setText(popText);

                donePop = new PopupWindow(
                        redeemPopover,
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                );

                if (Build.VERSION.SDK_INT >= 21) {
                    donePop.setElevation(5.0f);
                }

                Button closeButton = (Button) redeemPopover.findViewById(R.id.intDoneClose);

                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        donePop.dismiss();
                    }
                });

                donePop.showAtLocation(v, Gravity.CENTER, 0, 0);

                View container;
                if (donePop.getBackground() == null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        container = (View) donePop.getContentView().getParent();
                    } else {
                        container = donePop.getContentView();
                    }
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        container = (View) donePop.getContentView().getParent().getParent();
                    } else {
                        container = (View) donePop.getContentView().getParent();
                    }
                }

                WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
                p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                p.dimAmount = 0.8f;
                wm.updateViewLayout(container, p);

            } catch (JSONException e) {
                Log.e("JSON Error", e.getMessage(), e);
            }
        } else {
            Log.i("Error", "Parsing device JSON");
        }
    }
}
