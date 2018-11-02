package ca.cmpt276.walkinggroup.app;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import ca.cmpt276.walkinggroup.dataobjects.AllGroups;
import ca.cmpt276.walkinggroup.dataobjects.GpsLocation;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.model.State;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

/**
 * This is the walk activity. This can be access through the start walk activity;
 * it receives the intent destination latitude and longitude from the walk activity. Uploads user information to server every
 * 30 sec. Stops uploading when the user is close to the destination. (within 50m)
 */

public class WalkActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "WalkActivity";
    public static final String EXTRA_LAT = "ca.cmpt276.walkinggroup.app.WalkActivity - lat";
    public static final String EXTRA_LNG = "ca.cmpt276.walkinggroup.app.WalkActivity - lng";
    private Switch aSwitch;

    private State state;
    private WGServerProxy proxy;
    private User newUser;
    private AllGroups allGroups;

    private GoogleMap mMap;

    private GpsLocation currentlocation = new GpsLocation();

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private boolean mLocationPermissionsGranted = false;


    private FusedLocationProviderClient mFusedLocationProviderClient;
    private FusedLocationProviderClient client;
    private static final float DEFAULT_ZOOM = 15f;

    private Handler handler;
    private Handler tenSecsHandler =new Handler();
    private boolean checkSwitch = false;
    private boolean is10SwitchOn = false;
    private int walkPoints =0;


    Calendar calendar;
    String Date;
    SimpleDateFormat simpleDateFormat;
    Double destlat;
    Double destlng;
    float [] res = new float[100];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walk);
        state = State.getInstance();
        proxy = state.getProxy();
        newUser = state.getUser();
        aSwitch = (Switch)findViewById(R.id.switchLocation);
        allGroups = AllGroups.getInstance();

        getLocationPermission();
        extractDataFromIntent();
//        aSwitch.setChecked(true);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(checkSwitch){
            handler.removeCallbacksAndMessages(null);
        }
        if(is10SwitchOn){
            tenSecsHandler.removeCallbacksAndMessages(null);
        }

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        if (mLocationPermissionsGranted) {
            setupSwitch();
            dropMarker();
            getDeviceLocation();

            mMap.setMyLocationEnabled(true);
        }
    }


    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;
                initMap();
            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            Log.d(TAG, "onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }

    private void extractDataFromIntent(){
        Intent intent = getIntent();
        destlat = intent.getDoubleExtra(EXTRA_LAT, 0.0);
        destlng = intent.getDoubleExtra(EXTRA_LNG, 0.0);

    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapwalk);
        mapFragment.getMapAsync(WalkActivity.this);
    }


    private void getDeviceLocation() {
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionsGranted) {

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location!");
                            Location location = (Location) task.getResult();
                            Location.distanceBetween(location.getLatitude(),location.getLongitude(), destlat, destlng, res);
                            currentlocation.setLat(location.getLatitude());
                            currentlocation.setLng(location.getLongitude());
                            Log.d(TAG, "onComplete: current latitude is: " + currentlocation.getLat());

                            moveCamera(new LatLng(location.getLatitude(), location.getLongitude()),
                                    DEFAULT_ZOOM);
                            getDateAndTime();

                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(WalkActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }
    }


    ////////////////////////// UI FUNCTIONS  ////////////////

    private void moveCamera(LatLng latLng, float zoom) {
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }


    //////////////////////// WALK SPECIFIC FUNCTIONS ////////////////////////

    private void dropMarker(){
        Marker marker;

        marker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(destlat, destlng))
                .title("Destination")
                .snippet("This is where you want to go!")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
    }

    private void timer(){
        int delay = 30000;
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getDeviceLocation();
                updateLocationToServer();
                if(aSwitch.isChecked()) {
                    handler.postDelayed(this, delay);
                    if (res[0] < 100.0) {
                        is10SwitchOn = true;
                        int delay = 600000;
                        tenSecsHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(WalkActivity.this, " Reached destination!", Toast.LENGTH_SHORT).show();
                                walkPoints = 1000;
                                passPointsBackIntent();
                                finish();
                            }
                        }, delay);
                    }
                }
            }
        }, delay);
    }

    private void passPointsBackIntent() {
        Intent getPointsIntent = new Intent();
        getPointsIntent.putExtra("walkPoints", walkPoints);
        setResult(Activity.RESULT_OK, getPointsIntent);
    }

    private void getDateAndTime(){
        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date = simpleDateFormat.format(calendar.getTime());
        currentlocation.setTimestamp(Date);
    }



    private void updateLocationToServer(){
        Call<GpsLocation> caller = proxy.setLastGpsLocation(newUser.getId(), currentlocation);
        ProxyBuilder.callProxy(WalkActivity.this, caller, returnedGPS -> responseToSettingLocaion(returnedGPS));
    }

    private void responseToSettingLocaion(GpsLocation location){
        Log.d(TAG, "responseToSettingLocaion: current location is now set in the server! Time is " + location.getTimestamp());
        getDeviceLocation();

    }

    private void setupSwitch(){
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(aSwitch.isChecked()) {
                    checkSwitch = true;
                    Toast.makeText(WalkActivity.this, "Start uploading location", Toast.LENGTH_SHORT).show();
                    timer();

                    if (res[0] < 100.0) {
                        walkPoints = 1000;
                        passPointsBackIntent();
                    }
                }
                else{
                    checkSwitch = false;
                    Toast.makeText(WalkActivity.this, "Not currently uploading location!", Toast.LENGTH_SHORT).show();
                    if (res[0] < 100.0) {
                        walkPoints = 1000;
                        passPointsBackIntent();
                    }
                }
            }
        });

    }




///////////////////////// Intent ///////////////////////////////////////

    public static Intent makeWalkActivityIntent(Context context, Double lat, Double lng) {
        Intent intent = new Intent(context, WalkActivity.class);
        intent.putExtra(EXTRA_LAT, lat);
        intent.putExtra(EXTRA_LNG, lng);
        return intent;
    }
}
