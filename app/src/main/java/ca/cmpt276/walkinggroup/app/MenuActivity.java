package ca.cmpt276.walkinggroup.app;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.List;

import ca.cmpt276.walkinggroup.MapsActivity;
import ca.cmpt276.walkinggroup.dataobjects.AllGroups;
import ca.cmpt276.walkinggroup.dataobjects.Theme;
import ca.cmpt276.walkinggroup.dataobjects.GpsLocation;
import ca.cmpt276.walkinggroup.dataobjects.Group;
import ca.cmpt276.walkinggroup.model.State;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

/**
 * Main Menu page displays all important buttons leading to specific feature activities
 */
public class MenuActivity extends AppCompatActivity {
    private static final String TAG = "MenuActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    private static final String THEME_PREFS_NAME = "AppThemePrefs";
    private static final String CURRENT_THEME_PREF_NAME = "Current Theme";

    private State state = State.getInstance();
    WGServerProxy proxy = state.getProxy();
    private User newUser;
    private GpsLocation locations;

    private AllGroups allGroups;

    private ImageView iconImage;
    private TextView textTitle;

    SharedPreferences pref;
    SharedPreferences.Editor editor ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Theme.getCurrentTheme() == 0){
            Theme.setCurrentTheme(getThemeSelected(MenuActivity.this));
            Theme.setColourScheme(this);
        }
        else {
            Theme.setColourScheme(this);
        }
        //setTheme(Theme.getCurrentTheme());
        setContentView(R.layout.activity_menu);

        newUser = state.getUser();


        if (isServicesOK()){
            setupMapsButton();
            setupDashboard();
        }

        iconImage = (ImageView) findViewById(R.id.menuIconImage);
        textTitle = findViewById(R.id.txtCurrentTitle);

        allGroups = AllGroups.getInstance();

        callUser();

       //getUser();
        setUpAboutMeButton();
        //setupMonitoringOthersButton();
        setUpLogOutButton();
        //setupMonitoringMeButton();
        //setupGroupLeaderButton();
        //setupGroupsIAmInButton();
        setupAllGroupsButton();
        setupStartWalkButton();
        setupLeaderBoardButton();
        setupStoreButton();
        setupGroupsButton();
        setupMonitoringButton();
        setupResourcesButton();

        saveThemeSelected();



    }

    private void setupResourcesButton() {
        Button btn = findViewById(R.id.btnResources);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resourcesIntent = ResourcesActivity.makeResourcesIntent(MenuActivity.this);
                startActivity(resourcesIntent);

            }
        });
    }

    private void callUser(){
        Call<User> caller = proxy.getUserById(newUser.getId());
        ProxyBuilder.callProxy(MenuActivity.this, caller, returnedUser -> response(returnedUser));
    }
    private void response(User returnedUser){
        newUser = returnedUser;
        int iconID = newUser.getRewards().getCurrentIcon().getIconId();
        iconImage.setImageResource(iconID);
        String currentTitle = newUser.getRewards().getCurrentTitle().getTitleName();
        textTitle.setText(currentTitle);
    }

    //Needed when an activity is on the call stack when colour scheme is changed
    @Override
    protected void onRestart() {
        super.onRestart();
        Theme.changeTheme(this, Theme.getCurrentTheme());
        int iconID = newUser.getRewards().getCurrentIcon().getIconId();
        iconImage.setImageResource(iconID);
    }

    /////////////////////////SAVING THEMES
    private void saveThemeSelected() {
        SharedPreferences prefs = this.getSharedPreferences(THEME_PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(CURRENT_THEME_PREF_NAME, Theme.getCurrentTheme());
        editor.apply();
    }

    static public int getThemeSelected(Context context){
        SharedPreferences prefs = context.getSharedPreferences(THEME_PREFS_NAME, MODE_PRIVATE);
        return prefs.getInt(CURRENT_THEME_PREF_NAME , R.style.AppTheme);
    }

    private void setupStoreButton() {
        Button btn = findViewById(R.id.btnStore);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent storeMenuIntent = StoreMenuActivity.makeStoreMenuIntent(MenuActivity.this);
                startActivity(storeMenuIntent);

            }
        });
    }

    private void setupAllGroupsButton() {
        Call<List<Group>> caller = proxy.getGroups();
        ProxyBuilder.callProxy(MenuActivity.this, caller, returnedlistofgroups -> responsetolistofgroups(returnedlistofgroups));
    }

    private void responsetolistofgroups(List<Group> list){
        allGroups.setListogroups(list);
        //Toast.makeText(MenuActivity.this, "" + allGroups.getListogroups().get(0).getLeader().getId(), Toast.LENGTH_LONG).show();
    }

    private void setUpAboutMeButton() {
        Button btnAboutMe = findViewById(R.id.btnAboutMe);
        btnAboutMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent personalInfoIntent = EditInformationActivity.makeInfoIntent(MenuActivity.this, newUser.getId(), true);
                startActivity(personalInfoIntent);
            }
        });
    }


//    private void setupMonitoringOthersButton() {
//        Button monitoring = (Button) findViewById(R.id.btnMonitoring);
//        monitoring.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent monitoringOthersIntent = MonitoringActivity.makeMonitoringIntent(MenuActivity.this, true);
//                startActivity(monitoringOthersIntent);
//
//            }
//        });
//    }
//
//    private void setupMonitoringMeButton() {
//        Button btnMonitors = findViewById(R.id.btnMonitorMe);
//        btnMonitors.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent monitorMeIntent = MonitoringActivity.makeMonitoringIntent(MenuActivity.this, false);
//                startActivity(monitorMeIntent);
//
//            }
//        });
//    }

//    private void setupGroupLeaderButton() {
//        Button groupleader = (Button) findViewById(R.id.btnGroupLead);
//        groupleader.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent leadergroup = GroupsILeadActivity.makeGroupsILeadIntent(MenuActivity.this);
//                startActivity(leadergroup);
//            }
//        });
//    }
//
//    private void setupGroupsIAmInButton() {
//        Button btn = (Button)findViewById(R.id.btnGroups);
//        btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = GroupsIAmInActivity.makeGroupsIAmInIntent(MenuActivity.this);
//                startActivity(intent);
//            }
//        });
//    }

    private void setupMonitoringButton() {
        Button monitoring = (Button) findViewById(R.id.btnMonitoringMenu);
        monitoring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent monitoringIntent = MonitoringAndGroupsActivity.makeMonitoringAndGroupIntent(MenuActivity.this, false);
                startActivity(monitoringIntent);

            }
        });
    }

    private void setupGroupsButton() {
        Button monitoring = (Button) findViewById(R.id.btnGroupsMenu);
        monitoring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent groupsIntent = MonitoringAndGroupsActivity.makeMonitoringAndGroupIntent(MenuActivity.this, true);
                startActivity(groupsIntent);

            }
        });
    }
    //Sets token to null so that you are led to login again
    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK, checking google services version");
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MenuActivity.this);
        if(available == ConnectionResult.SUCCESS){
            //everything is fin and the user can make map requests
            Log.d(TAG, "iSServicesOK: Google Play Services is working");
            return true;
        }
        else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occurred but we can resolve it
            Log.d(TAG, "iSServicesOK: An error occurred but we can resolve it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(MenuActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }
        else{
            Toast.makeText(MenuActivity.this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }


    private void setupMapsButton() {
        Button maps = (Button) findViewById(R.id.btnMaps);
        maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent MapsIntent = MapsActivity.makeMapsIntent(MenuActivity.this);
                startActivity(MapsIntent);

            }
        });
    }

    private void setupDashboard() {
        Button btn = (Button)findViewById(R.id.btnParentDashboard);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // START THE PARENT DASHBOARD ACTIVITY----MAP ACTIVITY
                Intent MapsIntent = ParentDashboard.makeDashIntent(MenuActivity.this);
                startActivity(MapsIntent);
            }
        });
    }
    private void setupStartWalkButton() {
        Button btn = findViewById(R.id.btnStartAWalk);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = StartAWalkActivity.makeStartAWalkIntent(MenuActivity.this);
                startActivity(intent);

            }
        });
    }

    private void setupLeaderBoardButton(){
        Button btnLeaderBoard = findViewById(R.id.btnLeaderBoard);
        btnLeaderBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent leaderBoardIntent = LeaderBoardActivity.makeLeaderBoardIntent(MenuActivity.this);
                startActivity(leaderBoardIntent);
            }
        });
    }

    private void setUpLogOutButton() {
        Button btnLogOut = findViewById(R.id.btnLogout);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                state.Logout();

                SharedPreferences pref = getSharedPreferences("token",MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();

                editor.clear();
                editor.apply();

                Intent intent = MainActivity.makeMainIntent(MenuActivity.this);
                startActivity(intent);

                finish();
            }
        });
    }




    public static Intent makeMenuIntent(Context context) {
        return new Intent(context, MenuActivity.class);
    }

}
