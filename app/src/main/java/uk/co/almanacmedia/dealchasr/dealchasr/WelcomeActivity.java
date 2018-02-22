package uk.co.almanacmedia.dealchasr.dealchasr;

import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.ViewPager;

import android.view.View;

import uk.co.almanacmedia.dealchasr.dealspotr.R;

import static uk.co.almanacmedia.dealchasr.dealchasr.DoDeviceTouch.PREFS_NAME;

public class WelcomeActivity extends FragmentActivity {

    private static final int NUM_PAGES = 5;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_slider);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:  return new WelcomeFragmentOne();
                case 1:  return new WelcomeFragmentTwo();
                case 2:  return new WelcomeFragmentThree();
                case 3:  return new WelcomeFragmentFour();
                case 4:  return new WelcomeFragmentFive();
                default: return new WelcomeFragmentOne();
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }

    public void finishWelcomeScreen(View view){

        SharedPreferences settings = WelcomeActivity.this.getSharedPreferences(PREFS_NAME, 0);

        if(settings.contains("userID")){
            Intent intent = new Intent(WelcomeActivity.this, MainMapActivity.class);
            WelcomeActivity.this.startActivity(intent);
            ((Activity)WelcomeActivity.this).finish();
        } else {
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            WelcomeActivity.this.startActivity(intent);
            ((Activity)WelcomeActivity.this).finish();
        }
    }
}
