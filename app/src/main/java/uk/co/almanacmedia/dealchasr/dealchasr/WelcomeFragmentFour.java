package uk.co.almanacmedia.dealchasr.dealchasr;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uk.co.almanacmedia.dealchasr.dealchasr.R;

/**
 * Created by Sam on 21/02/2018.
 */

public class WelcomeFragmentFour extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.welcome_slide_four, container, false);

        return rootView;
    }
}
