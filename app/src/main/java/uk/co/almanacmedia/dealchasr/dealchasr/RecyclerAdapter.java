package uk.co.almanacmedia.dealchasr.dealchasr;

/**
 * Created by Sam on 19/02/2018.
 */

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

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    ArrayList<RecyclerModel> list;
    Context context;
    public PopupWindow mPopupWindow;
    public static final String PREFS_NAME = "DealSpotr.Data";
    RecyclerAdapter(Context context, ArrayList<RecyclerModel> list) {
        this.list = list;
        this.context = context;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        RecyclerModel voucher = list.get(position);
        final String voucherName = voucher.getVoucherName();
        String type = voucher.getType();
        String dealTitle;
        final Integer ID = voucher.getID();
        String time = voucher.getTime();
        Integer recurring = voucher.getRecurring();
        final String venueWebsite = voucher.getVenueWeb();
        final String venueName = voucher.getVenueName();
        final Integer venueID = voucher.getVenueID();
        final Boolean status = voucher.getStatus();
        final String voucherCount = voucher.getVoucherCount();
        final Integer daily = voucher.getDaily();

        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        final Integer userID = settings.getInt("userID", 0);

        if(type == "VOUCHER"){
            dealTitle = voucher.getVoucherName() + " - " + voucher.getDealName();
            holder.voucherCount.setText(voucherCount);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");

            try{
                Date mDate = sdf.parse(time);
                String nDate = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                Date nowDate = sdf.parse(nDate);
                long nowMilli = nowDate.getTime();
                long timeInMilliseconds = mDate.getTime();
                long diff = timeInMilliseconds - nowMilli;

                new CountDownTimer(diff, 1000){
                    public void onTick(long millisUntilFinished)  {
                        holder.timer.setText("DEAL ENDS: " + (TimeUnit.MILLISECONDS.toHours(millisUntilFinished) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(millisUntilFinished)))+":"
                        +(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished)))+":"
                        +(TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)))+"");
                    }

                    public void onFinish(){

                    }
                }.start();

            } catch (ParseException e){
                e.printStackTrace();
            }

            if(status == true){
                holder.redeemButton.setText("ALREADY REDEEMED");
                holder.redeemButton.setEnabled(false);
            } else {
                holder.redeemButton.setText("REDEEM NOW!");
            }
            holder.redeemButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ConstraintLayout mRelativeLayout = (ConstraintLayout) view.findViewById(R.id.main_venue);

                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                    View redeemPopover = inflater.inflate(R.layout.redeem_popover,null);

                    TextView vPrice = redeemPopover.findViewById(R.id.voucherPrice);
                    TextView vNameView = redeemPopover.findViewById(R.id.venueName);
                    vPrice.setText(voucherName);
                    vNameView.setText("@ " + venueName);

                    Button redeemNow = redeemPopover.findViewById(R.id.redeemNow);
                    redeemNow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new DoRedeemVoucher(context, ID, userID, mPopupWindow, view, holder, venueID).execute();
                        }
                    });

                    mPopupWindow = new PopupWindow(
                            redeemPopover,
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT
                    );

                    if(Build.VERSION.SDK_INT>=21){
                        mPopupWindow.setElevation(5.0f);
                    }

                    Button closeButton = (Button) redeemPopover.findViewById(R.id.ib_close);

                    closeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mPopupWindow.dismiss();
                        }
                    });

                    mPopupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

                    View container;
                    if (mPopupWindow.getBackground() == null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                            container = (View) mPopupWindow.getContentView().getParent();
                        } else {
                            container = mPopupWindow.getContentView();
                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            container = (View) mPopupWindow.getContentView().getParent().getParent();
                        } else {
                            container = (View) mPopupWindow.getContentView().getParent();
                        }
                    }

                    WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                    WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
                    p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                    p.dimAmount = 0.8f;
                    wm.updateViewLayout(container, p);
                }
            });
        } else if(type == "DEAL") {
            holder.title.setTextColor(Color.BLUE);
            holder.redeemButton.setTextColor(Color.BLUE);
            dealTitle = voucher.getDealName();
            if(status == true){
                holder.redeemButton.setText("YOU'RE GOING!");
                holder.redeemButton.setEnabled(false);
            } else {
                holder.redeemButton.setText("I'M INTERESTED!");
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");

            try{
                String dateString;
                if(recurring == 1){
                    if(daily == 1){
                        Date mDate = sdf.parse(time);
                        sdf = new SimpleDateFormat(" @ HH:mm");
                        dateString = "Every Day" + sdf.format(mDate);
                    } else {
                        Date mDate = sdf.parse(time);
                        sdf = new SimpleDateFormat("EEE dd @ HH:mm");
                        dateString = "Every " + sdf.format(mDate);
                    }
                } else if(daily == 1){
                    Date mDate = sdf.parse(time);
                    sdf = new SimpleDateFormat(" @ HH:mm");
                    dateString = "Every Day" + sdf.format(mDate);
                } else {
                    Date mDate = sdf.parse(time);
                    sdf = new SimpleDateFormat("EEE dd @ HH:mm");
                    dateString = sdf.format(mDate);
                }

                holder.timer.setText(dateString);
            } catch (ParseException e){
                e.printStackTrace();
            }

            holder.redeemButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ConstraintLayout mRelativeLayout = (ConstraintLayout) view.findViewById(R.id.main_venue);

                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                    View redeemPopover = inflater.inflate(R.layout.interested_popover, null);

                    Button webbutton = redeemPopover.findViewById(R.id.websiteButton);
                    Button goingbutton = redeemPopover.findViewById(R.id.goingButton);

                    webbutton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(venueWebsite));
                            context.startActivity(browserIntent);
                        }
                    });

                    goingbutton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            new DoMarkInterested(context, ID, userID, mPopupWindow, view, holder).execute();
                        }
                    });

                    mPopupWindow = new PopupWindow(
                            redeemPopover,
                            RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT
                    );

                    if (Build.VERSION.SDK_INT >= 21) {
                        mPopupWindow.setElevation(5.0f);
                    }

                    Button closeButton = (Button) redeemPopover.findViewById(R.id.ib_close);

                    closeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mPopupWindow.dismiss();
                        }
                    });

                    mPopupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

                    View container;
                    if (mPopupWindow.getBackground() == null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            container = (View) mPopupWindow.getContentView().getParent();
                        } else {
                            container = mPopupWindow.getContentView();
                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            container = (View) mPopupWindow.getContentView().getParent().getParent();
                        } else {
                            container = (View) mPopupWindow.getContentView().getParent();
                        }
                    }

                    WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
                    WindowManager.LayoutParams p = (WindowManager.LayoutParams) container.getLayoutParams();
                    p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                    p.dimAmount = 0.8f;
                    wm.updateViewLayout(container, p);
                }
            });
        } else {
            holder.title.setTextColor(Color.GRAY);
            holder.redeemButton.setVisibility(INVISIBLE);
            dealTitle = voucher.getVoucherName() + " " + voucher.getDealName();
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
        public Button redeemButton;
        public TextView timer;
        public TextView voucherPrice;
        public TextView voucherCount;

        public ViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.list_title);
            description = (TextView) view.findViewById(R.id.list_desc);
            redeemButton = (Button) view.findViewById(R.id.redeem);
            timer = (TextView) view.findViewById(R.id.countdown);
            voucherPrice = (TextView) view.findViewById(R.id.voucherPrice);
            voucherCount = view.findViewById(R.id.voucherCount);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }
}

