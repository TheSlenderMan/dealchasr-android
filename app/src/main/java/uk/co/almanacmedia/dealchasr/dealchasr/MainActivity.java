package uk.co.almanacmedia.dealchasr.dealchasr;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Timer;
import java.util.TimerTask;

import uk.co.almanacmedia.dealchasr.dealchasr.R;


public class MainActivity extends Activity {

    Context context;
    public static final String PREFS_NAME = "DealSpotr.Data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startFadeInAnimation();

        final SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        if(isNetworkAvailable()){
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    if(settings.contains("apitoken")){
                        new DoDeviceTouch(MainActivity.this).execute();
                    } else {
                        new DoGetAPIToken(MainActivity.this).execute();
                    }
                }
            }, 6000);
        } else {
            Toast.makeText(this, "Please check your internet connection and try again.", Toast.LENGTH_LONG).show();
            return;
        }

        FirebaseMessaging.getInstance().subscribeToTopic("news");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId;
            channelId = getString(R.string.default_notification_channel_id);
            String channelName = getString(R.string.default_notification_channel_name);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW));
        }

        overridePendingTransition(R.anim.empty, R.anim.slide_out);

    }

    public void startFadeInAnimation() {
        ImageView imageView = (ImageView) findViewById(R.id.splashLogo);
        Animation startAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        imageView.startAnimation(startAnimation);
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
