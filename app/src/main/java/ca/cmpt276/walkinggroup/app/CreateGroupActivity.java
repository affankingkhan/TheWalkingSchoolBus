package ca.cmpt276.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.Theme;
import ca.cmpt276.walkinggroup.dataobjects.GpsLocation;
import ca.cmpt276.walkinggroup.dataobjects.Group;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.model.State;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

/**
 * allows you to create a group from by entering a start location like SFU and end location like UBC
 * along with a description.
 */

public class CreateGroupActivity extends AppCompatActivity {

    private static final String TAG = "CreateGroupActivity";
    private State state;
    WGServerProxy proxy;
    private User newUser;
    //private GpsLocation gpsLocation;
    private GpsLocation sourceGPS;
    private GpsLocation destGPS;

    EditText sourceedit;
    EditText destedit;
    EditText description;

    double[] latarray;
    double[] longarray;

    private Group group;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Theme.setColourScheme(this);
        setContentView(R.layout.activity_create_group);

        group = new Group();

        state = State.getInstance();
        proxy = state.getProxy();
        newUser = state.getUser();
        sourceGPS = new GpsLocation();
        destGPS = new GpsLocation();

        //gpsLocation = GpsLocation.getInstance();


        latarray = new double[2];
        longarray = new double[2];


        setupCreateGroupButton();
        setupBackButton();

    }

    private void setupBackButton() {
        Button back = (Button) findViewById(R.id.btnBackCreateGroup);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setupCreateGroupButton() {
        Button creategroup = (Button) findViewById(R.id.btnCreateCreateGroup);
        creategroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sourceedit = (EditText)findViewById(R.id.txtStarting);
                destedit = (EditText)findViewById(R.id.txtDestination);
                description = (EditText)findViewById(R.id.txtDescription) ;
                String descriptionString = description.getText().toString();


                geoLocate(sourceedit);
                geoLocate(destedit);
                group.setGroupDescription(descriptionString);
                group.setRouteLatArray(latarray);
                group.setRouteLngArray(longarray);
                group.setLeader(newUser);


                Call<Group> caller = proxy.createGroup(group);
                ProxyBuilder.callProxy(CreateGroupActivity.this, caller, returnedGroup -> response(returnedGroup));
            }

            private void response(Group group){
//                Intent leadsActivityIntent = GroupsILeadActivity.makeGroupsILeadIntent(CreateGroupActivity.this);
//                startActivity(leadsActivityIntent);
                finish();

            }

        });

    }

    private void geoLocate(EditText editText){
        Log.d(TAG, "geoLocate: Geolocating" );
        String string = editText.getText().toString();

        Geocoder geocoder = new Geocoder(CreateGroupActivity.this);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(string, 1);
        }catch(IOException e){
            Log.e(TAG, "geoLocate: IOException: " + e.getMessage() );
        }
        if(list.size() > 0){
            Address address = list.get(0);
            if (editText == sourceedit){
                sourceGPS.setLat(address.getLatitude());
                sourceGPS.setLng(address.getLongitude());
                //gpsLocation.setSourceAddress(address);
                latarray[0] = address.getLatitude();
                longarray[0] = address.getLongitude();
            }
            else if (editText == destedit){
                destGPS.setLat(address.getLatitude());
                destGPS.setLng(address.getLongitude());
                //gpsLocation.setDestinationAddress(address);
                latarray[1] = address.getLatitude();
                longarray[1] = address.getLongitude();
            }
            Log.d(TAG, "geoLocate: FOUND THE  LOCATION: " + address.toString() );
        }
        else{
            Log.d(TAG, "geoLocate: Couldn't find location" );
        }
    }



    public static Intent makeCreateGroupIntent(Context context) {
        return new Intent(context, CreateGroupActivity.class);
    }




}
