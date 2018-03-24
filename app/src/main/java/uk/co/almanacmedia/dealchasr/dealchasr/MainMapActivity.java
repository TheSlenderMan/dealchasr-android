package uk.co.almanacmedia.dealchasr.dealchasr;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.Manifest;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.location.LocationServices;

import uk.co.almanacmedia.dealchasr.dealchasr.R;

public class MainMapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap map;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private ArrayAdapter<String> mAdapter;
    private ListView lv;

    public static final String PREFS_NAME = "DealSpotr.Data";

    private Bitmap smallMarker;
    private Bitmap smallMarker1;
    private Bitmap smallMarker3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_map);

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.ds_toolbar);
        toolbar.setNavigationIcon(R.drawable.hamburger_menu);
        toolbar.setTitle("");
        toolbar.setSubtitle("");
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.app_name,R.string.app_name);
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        lv = findViewById(R.id.left_drawer);
        addDrawerItems();

        Integer height = 150;
        Integer width = 145;
        Integer height2 = 190;
        Integer width2 = 185;
        BitmapDrawable bitmapDraw;
        BitmapDrawable bitmapDraw1;
        BitmapDrawable bitmapDraw2;
        Bitmap b;
        Bitmap b1;
        Bitmap b2;

        bitmapDraw = (BitmapDrawable) getResources().getDrawable(R.drawable.mappin);
        b = bitmapDraw.getBitmap();
        smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

        bitmapDraw1 = (BitmapDrawable) getResources().getDrawable(R.drawable.mappinnone);
        b1 = bitmapDraw1.getBitmap();
        smallMarker1 = Bitmap.createScaledBitmap(b1, width, height, false);

        bitmapDraw2 = (BitmapDrawable) getResources().getDrawable(R.drawable.mappinpro);
        b2 = bitmapDraw2.getBitmap();
        smallMarker3 = Bitmap.createScaledBitmap(b2, width2, height2, false);

    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    private void addDrawerItems() {
        String[] osArray = { "MY VOUCHERS", "MY INTERESTS", "HOW TO USE DEALCHASR", "LOG OUT" };
        mAdapter = new ArrayAdapter<String>(this, R.layout.menu_item, osArray);
        lv.setAdapter(mAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(isNetworkAvailable()){
                    if(position == 0){
                        Intent intent = new Intent(MainMapActivity.this, MyVouchersActivity.class);
                        MainMapActivity.this.startActivity(intent);
                        ((Activity)MainMapActivity.this).finish();
                    }
                    if(position == 1){
                        Intent intent = new Intent(MainMapActivity.this, MyInterestsActivity.class);
                        MainMapActivity.this.startActivity(intent);
                        ((Activity)MainMapActivity.this).finish();
                    }
                    if(position == 2){
                        Intent intent = new Intent(MainMapActivity.this, WelcomeActivity.class);
                        MainMapActivity.this.startActivity(intent);
                        ((Activity)MainMapActivity.this).finish();
                    }
                    if(position == 3){
                        final ProgressDialog PD = new ProgressDialog(MainMapActivity.this, R.style.CustomDialog);
                        PD.setMessage("Logging Out...");
                        PD.setCancelable(false);
                        PD.show();

                        SharedPreferences settings = MainMapActivity.this.getSharedPreferences(PREFS_NAME, 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.clear();
                        editor.apply();

                        new Handler().postDelayed(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        PD.dismiss();

                                        Intent intent = new Intent(MainMapActivity.this, LoginActivity.class);
                                        MainMapActivity.this.startActivity(intent);
                                        ((Activity)MainMapActivity.this).finish();
                                    }
                                },
                                400
                        );
                    }
                } else {
                    Toast.makeText(MainMapActivity.this, "Please check your internet connection.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    private void getMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainMapActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);

            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            this.map.setMyLocationEnabled(true);
            this.map.setOnMyLocationButtonClickListener(this);
            this.map.setOnMyLocationClickListener(this);
            this.map.setPadding(0, 150, 0, 0);

            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, true);
            Location location = locationManager.getLastKnownLocation(provider);


            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                LatLng latLng = new LatLng(latitude, longitude);
                LatLng myPosition = new LatLng(latitude, longitude);


                LatLng coordinate = new LatLng(latitude, longitude);
                CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 13);
                map.moveCamera(yourLocation);

                if(isNetworkAvailable()){
                    new DoGetVenues(map, MainMapActivity.this, smallMarker, smallMarker1, smallMarker3).execute();
                } else {
                    Toast.makeText(this, "Please check your internet connection.", Toast.LENGTH_LONG).show();
                }
            }

        }else{
            Toast.makeText(MainMapActivity.this,
                    "mLastLocation == null",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy(){
        smallMarker.recycle();
        smallMarker1.recycle();
        smallMarker3.recycle();
        super.onDestroy();
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        //Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        //Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainMapActivity.this,
                            "Location services are on",
                            Toast.LENGTH_LONG).show();
                    getMyLocation();

                } else {
                    Toast.makeText(MainMapActivity.this,
                            "Location services are off.",
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {

        this.map = map;
        this.map.setOnMarkerClickListener(this);
        this.map.setOnInfoWindowClickListener(this);


        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected()) {
                if(isNetworkAvailable()){
                    getMyLocation();
                } else {
                    Toast.makeText(this, "Please check your internet connection.", Toast.LENGTH_LONG).show();
                }
            }
        }

    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        map.clear();
        System.gc();
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getMyLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(MainMapActivity.this,
                "onConnectionSuspended: " + String.valueOf(i),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(MainMapActivity.this,
                "onConnectionFailed: \n" + connectionResult.toString(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        /*Integer venueID = (Integer) marker.getTag();

        Intent intent = new Intent(MainMapActivity.this, VenueFragment.class);
        intent.putExtra("VID", (Integer) venueID);
        intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
        MainMapActivity.this.startActivity(intent);
        ((Activity)MainMapActivity.this).finish();*/



        return false;
    }

    @Override
    public void onInfoWindowClick(final Marker marker){
        Integer venueID = (Integer) marker.getTag();

        Intent intent = new Intent(MainMapActivity.this, VenueFragment.class);
        intent.putExtra("VID", (Integer) venueID);
        intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
        MainMapActivity.this.startActivity(intent);
        ((Activity)MainMapActivity.this).finish();
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
