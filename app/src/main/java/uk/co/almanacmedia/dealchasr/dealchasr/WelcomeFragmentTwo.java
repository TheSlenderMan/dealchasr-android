package uk.co.almanacmedia.dealchasr.dealchasr;


import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import uk.co.almanacmedia.dealchasr.dealchasr.R;

/**
 * Created by Sam on 10/02/2018.
 */

public class WelcomeFragmentTwo extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.welcome_slide_two, container, false);

        return rootView;
    }
}
