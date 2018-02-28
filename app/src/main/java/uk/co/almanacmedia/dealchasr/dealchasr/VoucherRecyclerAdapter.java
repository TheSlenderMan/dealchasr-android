package uk.co.almanacmedia.dealchasr.dealchasr;

/**
 * Created by Sam on 23/02/2018.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import uk.co.almanacmedia.dealchasr.dealchasr.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.view.View.INVISIBLE;

public class VoucherRecyclerAdapter extends RecyclerView.Adapter<VoucherRecyclerAdapter.ViewHolder> {
    ArrayList<VoucherRecyclerModel> list;
    Context context;
    public PopupWindow mPopupWindow;
    public static final String PREFS_NAME = "DealSpotr.Data";
    VoucherRecyclerAdapter(Context context, ArrayList<VoucherRecyclerModel> list) {
        this.list = list;
        this.context = context;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.voucher_card_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        VoucherRecyclerModel voucher = list.get(position);
        final String voucherName = voucher.getVoucherName();
        final String dealTitle;
        final Integer ID = voucher.getID();
        final String time = voucher.getTime();
        final String venueName = voucher.getVenueName();
        final String voucherCount = voucher.getVoucherCount();

        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        final Integer userID = settings.getInt("userID", 0);

        dealTitle = voucher.getVoucherName() + " - " + voucher.getDealName() + "\n@ " + venueName;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");

        try{
            Date mDate = sdf.parse(time);
            String nDate = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            Date nowDate = sdf.parse(nDate);
            long nowMilli = nowDate.getTime();
            long timeInMilliseconds = mDate.getTime();
            long diff = timeInMilliseconds - nowMilli;

            if(nowMilli > timeInMilliseconds){
                holder.timer.setText("EXPIRED");
                holder.viewButton.setEnabled(false);
                holder.viewButton.setText("");
            } else {
                new CountDownTimer(diff, 1000){
                    public void onTick(long millisUntilFinished)  {
                        holder.timer.setText("DEAL ENDS: " + (TimeUnit.MILLISECONDS.toHours(millisUntilFinished) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(millisUntilFinished)))+":"
                                +(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)))+":"
                                +(TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)))+"");
                    }

                    public void onFinish(){

                    }
                }.start();

                holder.viewButton.setText("VIEW VOUCHER");
                holder.viewButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, ViewVoucherActivity.class);
                        intent.putExtra("userID", (Integer) userID);
                        intent.putExtra("voucherID", (Integer) ID);
                        context.startActivity(intent);
                        ((Activity)context).finish();
                    }
                });
            }
        } catch (ParseException e){
            e.printStackTrace();
        }

        String dealDescription = voucher.getDescriptionName();
        holder.title.setText(dealTitle);
        holder.description.setText(dealDescription);

    }

    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView description;
        public Button viewButton;
        public TextView timer;

        public ViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.my_list_title);
            description = (TextView) view.findViewById(R.id.my_list_desc);
            viewButton = (Button) view.findViewById(R.id.view_voucher);
            timer = (TextView) view.findViewById(R.id.my_countdown);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }
}

