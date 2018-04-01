package uk.co.almanacmedia.dealchasr.dealchasr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import uk.co.almanacmedia.dealchasr.dealchasr.R;

/**
 * Created by Sam on 18/02/2018.
 */

public class VenueFragment extends Activity {

    private Integer VID;
    private RecyclerView recyclerView;
    private Double vlat;
    private Double vlong;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.venue_view);

        recyclerView = (RecyclerView) findViewById(R.id.cardView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        this.VID = getIntent().getIntExtra("VID", 0);
        this.vlat = getIntent().getDoubleExtra("vlat", 0);
        this.vlong = getIntent().getDoubleExtra("vlong", 0);

        overridePendingTransition(R.anim.slide_in, R.anim.empty);

        new DoGetVenue(VenueFragment.this, this.VID, findViewById(android.R.id.content), recyclerView, vlat, vlong).execute();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(VenueFragment.this, MainMapActivity.class);
        VenueFragment.this.startActivity(intent);
        ((Activity)VenueFragment.this).finish();
    }

    public void BackToMap(View v){

        Intent intent = new Intent(VenueFragment.this, MainMapActivity.class);
        VenueFragment.this.startActivity(intent);
        ((Activity)VenueFragment.this).finish();

        overridePendingTransition(R.anim.empty, R.anim.slide_out);
    }
}
