package ca.cmpt276.walkinggroup;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.nfc.Tag;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ca.cmpt276.walkinggroup.app.GroupsILeadActivity;
import ca.cmpt276.walkinggroup.app.MenuActivity;
import ca.cmpt276.walkinggroup.app.R;
import ca.cmpt276.walkinggroup.dataobjects.GpsLocation;
import ca.cmpt276.walkinggroup.dataobjects.Group;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.model.State;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

/**
 * Click the show groups button to view all the group. click the marker and then click the window to join that
 * group. You can also create a group from this activity
 */


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private State state;
    WGServerProxy proxy;
    private User newUser;
//    private GpsLocation gpsLocation;

    private GpsLocation sourceGPS;
    private GpsLocation destinationGPS;

    private static final String TAG = "MapActivity";
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;


    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final float DEFAULT_ZOOM = 15f;


    private HashMap<Marker, Long> mHash = new HashMap<>();
    private Marker[] markersarray;


    //WIDGETS

    private EditText mStartText;
    private EditText mDesText;
    private EditText mDescriptionText;

    double[] latarray;
    double[] longarray;

    private Group group;

    private String[] GroupDesciptions;
    private Long [] GroupIDs;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        state = State.getInstance();
        proxy = state.getProxy();
        newUser = state.getUser();
        //gpsLocation = GpsLocation.getInstance();
        sourceGPS = new GpsLocation();
        destinationGPS = new GpsLocation();
        latarray = new double[2];
        longarray = new double[2];
        group = new Group();


        mStartText = (EditText)findViewById(R.id.input_search);
        mDesText = (EditText)findViewById(R.id.input_search2);
        mDescriptionText = findViewById(R.id.input_description1);

        getLocationPermission();

    }



    private void init(){
        Log.d(TAG, "Init: Initializing" );
        Button Accept = (Button) findViewById(R.id.btnAcceptSource);
        Accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String descriptionString = mDescriptionText.getText().toString();

                HideSoftKeyboard();

                Log.d(TAG, "Init: start" );

                if (descriptionString.isEmpty()){
                    Toast.makeText(MapsActivity.this, " Invalid input! Must have group description!", Toast.LENGTH_SHORT).show();
                }
                else{
                    geoLocate(mStartText);
                    Log.d(TAG, "Init: GOT SOURCE LOCATION" );

                    geoLocate(mDesText);
                    Log.d(TAG, "Init: GOT DESTINATION LOCATION" );
                    if (latarray[0] != 0 && latarray[1] != 0 && longarray[0] != 0 && longarray[1] != 0 ){
                        group.setLeader(newUser);
                        group.setRouteLatArray(latarray);
                        group.setRouteLngArray(longarray);
                        group.setGroupDescription(descriptionString);

                        Call<Group> caller = proxy.createGroup(group);
                        ProxyBuilder.callProxy(MapsActivity.this, caller, returnedGroup -> response(returnedGroup));

                    }
                    else{
                        Toast.makeText(MapsActivity.this, " Invalid locations!", Toast.LENGTH_SHORT).show();
                    }


                }
            }

            private void response(Group group){
                Log.d(TAG, "response: Successfully create a group!");
            }
        });

    }


    private void geoLocate(EditText editText){
        Log.d(TAG, "geoLocate: Geolocating" );
        String string = editText.getText().toString();

        Geocoder geocoder = new Geocoder(MapsActivity.this);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(string, 1);
        }catch(IOException e){
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage() );
        }
        if(list.size() > 0){
            Address address = list.get(0);
            if(address.getLatitude() != 0 && address.getLongitude() != 0){
                if (editText == mStartText){
                    sourceGPS.setLat(address.getLatitude());
                    sourceGPS.setLng(address.getLongitude());
                    latarray[0] = address.getLatitude();
                    longarray[0] = address.getLongitude();
                    //gpsLocation.setSourceAddress(address);

                }
                else if (editText == mDesText){
                    destinationGPS.setLat(address.getLatitude());
                    destinationGPS.setLng(address.getLongitude());
                    latarray[1] = address.getLatitude();
                    longarray[1] = address.getLongitude();
                    //gpsLocation.setDestinationAddress(address);
                    moveCamera(new LatLng(destinationGPS.getLat(), destinationGPS.getLng()), DEFAULT_ZOOM );
                }
                Log.d(TAG, "geoLocate: FOUND THE  LOCATION: " + address.toString() );
                DropPin(new LatLng(address.getLatitude(),address.getLongitude()), address.getAddressLine(0));

            }


        }
        else{
            Log.d(TAG, "geoLocate: Couldn't find location" );
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        if (mLocationPermissionsGranted) {
            getDeviceLocation();
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            setupShowGroups();
            init();
        }
    }


    private void initMap(){
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(MapsActivity.this);
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
                            Toast.makeText(MapsActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }catch (SecurityException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage() );
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



    private void getAllGroup(){
        Call<List<Group>> caller = proxy.getGroups();
        ProxyBuilder.callProxy(MapsActivity.this, caller, returnedlistofgroups -> responsetolistofgroups(returnedlistofgroups));

    }

    private void responsetolistofgroups(List<Group> listofgroups){

        GroupDesciptions = new String[listofgroups.size()];
        GroupIDs = new Long[listofgroups.size()];
        markersarray = new Marker[listofgroups.size()];


        for(int i = 0; i< listofgroups.size(); i++){
            GroupDesciptions[i] = listofgroups.get(i).getGroupDescription();
            GroupIDs[i] = listofgroups.get(i).getId();
            double[] lat;
            double[] lon;

            lat = listofgroups.get(i).getRouteLatArray();
            lon = listofgroups.get(i).getRouteLngArray();
            if(lat[0] != 0 && lat[1] != 0 && lon[0] != 0 && lon[1] != 0 ){
                Marker marker;
                marker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(lat[1], lon[1]))
                        .title(listofgroups.get(i).getGroupDescription())
                        .snippet("Click to join group!")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                markersarray[i] = marker;
                mHash.put(marker, listofgroups.get(i).getId());

                Log.d(TAG, "GET ALL GROUPS Destinations: " + lat[1] + " and " + lon[1]);

            }


        }
        //Toast.makeText(MapsActivity.this, ""+ mHash.get(markersarray[4]), Toast.LENGTH_LONG).show();

    }

    private void setupMarkerClick(){
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Long groupId = mHash.get(marker);
                Call<List<User>> caller = proxy.addGroupMember(groupId, newUser);
                ProxyBuilder.callProxy(MapsActivity.this, caller, returnedlistofuser -> responsetolistofusers(returnedlistofuser));

            }

            private void responsetolistofusers(List<User> list){
                Toast.makeText(MapsActivity.this, ""+ list.get(0).getEmail(), Toast.LENGTH_LONG).show();
            }
        });


    }




//////////////////////////////////////////// UI FUNCTIONS ////////////////////////////////////////




    private void moveCamera(LatLng latLng, float zoom){
        Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }


    private void HideSoftKeyboard(){
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void DropPin(LatLng latLng, String title){
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .snippet("New created group!")
                .title(title);
        mMap.addMarker(options);

    }

//MARKER FOR GROUPS
//    private void DropGroupsPin(MarkerOptions options,LatLng latLng, String title){
//        MarkerOptions options = new MarkerOptions();
//                options.position(latLng);
//                options.title(title);
//                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
//        mMap.addMarker(options);
//
//    }


    private void setupShowGroups(){
        Button showgroups = (Button) findViewById(R.id.btnShowGroups);
        showgroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAllGroup();
                setupMarkerClick();
            }
        });
    }





/////////////////////////////////////////////////  INTENTS ////////////////////////////////////////////////////////////////////////
    public static Intent makeMapsIntent(Context context) {
        return new Intent(context, MapsActivity.class);
    }


}
