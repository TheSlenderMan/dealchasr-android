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

import uk.co.almanacmedia.dealchasr.dealspotr.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.view.View.INVISIBLE;

public class InterestsRecyclerAdapter extends RecyclerView.Adapter<InterestsRecyclerAdapter.ViewHolder> {
    ArrayList<InterestsRecyclerModel> list;
    Context context;
    public PopupWindow mPopupWindow;
    public static final String PREFS_NAME = "DealSpotr.Data";
    InterestsRecyclerAdapter(Context context, ArrayList<InterestsRecyclerModel> list) {
        this.list = list;
        this.context = context;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.interest_card_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        InterestsRecyclerModel voucher = list.get(position);
        final String voucherName = voucher.getVoucherName();
        final String dealTitle;
        final Integer ID = voucher.getID();
        final String time = voucher.getTime();
        final String venueName = voucher.getVenueName();
        final Integer recurring = voucher.getRecurring();

        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        final Integer userID = settings.getInt("userID", 0);

        holder.title.setTextColor(Color.BLUE);
        dealTitle = voucher.getDealName();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");

        try{
            String dateString;
            if(recurring == 1){
                Date mDate = sdf.parse(time);
                sdf = new SimpleDateFormat("EEE dd @ HH:mm");
                dateString = "Every " + sdf.format(mDate);
            } else {
                Date mDate = sdf.parse(time);
                sdf = new SimpleDateFormat("EEE dd @ HH:mm");
                dateString = sdf.format(mDate);
            }

            holder.timer.setText(dateString);
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
        public TextView timer;

        public ViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.myd_list_title);
            description = (TextView) view.findViewById(R.id.myd_list_desc);
            timer = (TextView) view.findViewById(R.id.myd_countdown);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }
}

