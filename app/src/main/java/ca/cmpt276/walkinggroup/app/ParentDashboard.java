package ca.cmpt276.walkinggroup.app;

import android.Manifest;
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
import android.view.View;
import android.widget.Button;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ca.cmpt276.walkinggroup.dataobjects.AllGroups;
import ca.cmpt276.walkinggroup.dataobjects.GpsLocation;
import ca.cmpt276.walkinggroup.dataobjects.Group;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.model.State;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

/**
 * THI is the parent dashboard map activity. Parents can see all their children by clicking the show user
 * button. Blue marker should appear where the users last location was. You can only click the show leader button
 * when the show user button is clicked. It shows the locations of the leader of your child's walking group
 */
public class ParentDashboard extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "ParentDashboard";

    private State state;
    private WGServerProxy proxy;
    private User newUser;
    private AllGroups allGroups;
    private List<Group> allthegroups = new ArrayList<>();

    private HashMap<Marker, User> mHash = new HashMap<>();
    private HashMap<Marker, User> mLeaderHash = new HashMap<>();

    private List<User> MoniteredUsers;
    private List<User> listLeaders = new ArrayList<>();
    private List<User> listServerLeaders =new ArrayList<>();
    private List<GpsLocation> listLocations = new ArrayList<>();

    private List<Long> listGroupIDs = new ArrayList<>();
    private List<Long> listLeaderIDs;

    private Marker[] usermarkerarray;
    private Marker[] leadermarkerarray;

    private Handler handler;



    private GoogleMap mMap;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private Boolean mLocationPermissionsGranted = false;


    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final float DEFAULT_ZOOM = 15f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_dashboard);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        state = State.getInstance();
        proxy = state.getProxy();
        newUser = state.getUser();
        allGroups = AllGroups.getInstance();
        allthegroups = allGroups.getListogroups();
        MoniteredUsers = new ArrayList<>();
        listLeaderIDs = new ArrayList<>();

        getLocationPermission();
    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        if (mLocationPermissionsGranted) {
            getDeviceLocation();
            mMap.setMyLocationEnabled(true);
            setupShowUsers();
        }
    }


    private void getLocationPermission(){
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initMap();
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called.");
        mLocationPermissionsGranted = false;

        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
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

    private void initMap(){
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.dashmap);
        mapFragment.getMapAsync(ParentDashboard.this);
    }



    private void getDeviceLocation(){
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try{
            if(mLocationPermissionsGranted){

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "onComplete: found location!");
                            Location currentLocation = (Location) task.getResult();

                            moveCamera(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),
                                    DEFAULT_ZOOM);

                        }else{
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(ParentDashboard.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
        }
    }


    ////////////////////////// UI FUNCTIONS  ////////////////

    private void moveCamera(LatLng latLng, float zoom){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }


    ////////////////////////////////// Dashboard specific functions///////////////////////////////////

    private void timer(){
        int delay = 10000;
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getMonitoredUsers();
            }
        }, delay);
    }


    private void setupShowUsers(){
        Button btn = (Button) findViewById(R.id.btnShowUsers);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timer();
            }
        });
    }


    private void getMonitoredUsers(){
        Call<List<User>> caller = proxy.getMonitorsUsers(newUser.getId());
        ProxyBuilder.callProxy(ParentDashboard.this, caller, returnedlist -> responsetolistofmonitoredusers(returnedlist));
    }

    private void responsetolistofmonitoredusers(List<User> userList){
        if(userList.size() != 0){
            MoniteredUsers = userList;
            usermarkerarray = new Marker[userList.size()];
            //Log.d(TAG, "responsetolistofmonitoredusers: Group ID = " + userList.get(1).getMemberOfGroups().get(0).getId());

            for (int i=0; i<userList.size(); i++){
                User user = new User();
                user = userList.get(i);
                GpsLocation lastlocation = new GpsLocation();
                lastlocation = user.getLastGpsLocation();

                if(lastlocation.getLat() != null && lastlocation.getLng() != null){
                    Marker marker;

                    marker = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(lastlocation.getLat(), lastlocation.getLng()))
                            .title("Name: " + user.getName())
                            .snippet("Last Updated Location: " + user.getLastGpsLocation().getTimestamp())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                    usermarkerarray[i] = marker;
                    mHash.put(marker, user);

                    List<Group> memberofgroup = new ArrayList<>();
                    memberofgroup = user.getMemberOfGroups();
                    for (int j =0; j<memberofgroup.size(); j++){
                        listGroupIDs.add(memberofgroup.get(j).getId());
                    }
                }
            }
            //Log.d(TAG, "responsetolistofmonitoredusers: CHECK IF GROUP ID WAS STORED " + listGroupIDs.get(0));

            for (int i = 0; i<listGroupIDs.size(); i++){
                for (int j = 0; j<allthegroups.size(); j++){
                    if (listGroupIDs.get(i).equals(allthegroups.get(j).getId())){
                        User leader = new User();
                        leader = allthegroups.get(j).getLeader();
                        listLeaders.add(leader);
                    }
                }
            }
            //Log.d(TAG, "responsetolistofmonitoredusers: CHECK IF LEADERS WERE STORED " + listLeaders.get(0).getId());
            setupShowLeaders();
        }
        else{
            Toast.makeText(ParentDashboard.this, "You are not monitoring anyone!", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "AFFAN");
        }

    }

    private void setupShowLeaders(){
        Button leaderbtn = (Button) findViewById(R.id.btnShowLeaders);
        leaderbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getListOfLeaders();

            }
        });
    }


    private void getListOfLeaders(){
        for (int i = 0; i< listLeaders.size(); i++){
            Call<User> caller = proxy.getUserById(listLeaders.get(i).getId());
            ProxyBuilder.callProxy(ParentDashboard.this, caller, returnedserveruser -> responsetoserveruser(returnedserveruser));
        }

    }
    private void responsetoserveruser( User serveruser){

        GpsLocation lastlocation = new GpsLocation();
        lastlocation = serveruser.getLastGpsLocation();

        if(lastlocation.getLat() != null && lastlocation.getLng() != null) {
            Marker marker;

            marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lastlocation.getLat(), lastlocation.getLng()))
                    .title("Leader Name: " + serveruser.getName())
                    .snippet("Last Updated Location: " + serveruser.getLastGpsLocation().getTimestamp())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        }
    }




////////////////////////////////// INTENT /////////////////////////////////////////////

    public static Intent makeDashIntent(Context context) {
        return new Intent(context, ParentDashboard.class);
    }
}
