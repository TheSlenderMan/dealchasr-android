package uk.co.almanacmedia.dealchasr.dealchasr;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

import uk.co.almanacmedia.dealchasr.dealspotr.R;


public class MainActivity extends Activity {

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startFadeInAnimation();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                new DoDeviceTouch(MainActivity.this).execute();
            }
        }, 6000);

        overridePendingTransition(R.anim.empty, R.anim.slide_out);

    }

    public void startFadeInAnimation() {
        ImageView imageView = (ImageView) findViewById(R.id.splashLogo);
        Animation startAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        imageView.startAnimation(startAnimation);
    }

}
