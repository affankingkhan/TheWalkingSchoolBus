package ca.cmpt276.walkinggroup.app;


import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.Theme;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.model.State;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

/**
 * Activity allows a User(parent) to add another User to monitor (child) by their email and vice versa.
 * Confirm button/cancel/back button lead to updated “List of Users Who I Monitor/ List of Users Who Monitor Me”
 */

public class AddPersonToMonitoringListsActivity extends AppCompatActivity{

    private static final String TAG ="Add Monitoring Others" ;
    private static final String EXTRA_IS_USER_TO_MONITOR = "ca.cmpt276.walkinggroup.app, AddPersonToMonitoringListsActivity  - is Add User I Monitor?"; ;

    private Long currentUserId;
    private WGServerProxy proxy;

    boolean isAddUserIMonitor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Theme.setColourScheme(this);
        setContentView(R.layout.activity_add_person_to_monitoring_lists);

        State state = State.getInstance();

        proxy = state.getProxy();
        currentUserId = state.getUser().getId();

        extractDataFromIntent();
        setupConfirmButton();
        setupCancelButton();
    }

    //Add Monitor Others Activity can create itself
    public static Intent AddToMonitoringIntent(Context context, boolean isAddUserToMonitor) {
        Intent intent = new Intent(context, AddPersonToMonitoringListsActivity.class);
        intent.putExtra(EXTRA_IS_USER_TO_MONITOR, isAddUserToMonitor);
        return intent;
    }

    private void extractDataFromIntent() {

        Intent intent = getIntent();
        isAddUserIMonitor = intent.getBooleanExtra(EXTRA_IS_USER_TO_MONITOR, false);
        updateUI();
    }

    private void updateUI() {

        TextView title = findViewById(R.id.txtAddPerson);
        if(isAddUserIMonitor){
            title.setText(R.string.a_M_O_title);
        }
        else{
            title.setText(R.string.title_monitoring_me);
        }
    }

    private void setupConfirmButton() {

        Button addPerson = findViewById(R.id.btnConfirm);
        addPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText emailText = findViewById(R.id. txtMonitorMeEmail);

                //Extract email of person I want to monitor from UI
                String emailString = emailText.getText().toString();

                //Check if Email is valid by checking with server
                Call<User> caller = proxy.getUserByEmail(emailString);
                ProxyBuilder.callProxy(AddPersonToMonitoringListsActivity.this, caller,
                        returnedUser -> response(returnedUser));

            }
        });
    }

    private void response(User user) {

        if(isAddUserIMonitor) {

            Call<List<User>> caller = proxy.addToMonitorsUsers(currentUserId, user);
            ProxyBuilder.callProxy(this, caller, returnedUser -> responseAdd(returnedUser));
        }
        else{

            Call<List<User>> caller = proxy.addToMonitoredByUsers(currentUserId, user);
            ProxyBuilder.callProxy(this, caller, returnedUser -> responseAdd(returnedUser));
        }

    }

    private void responseAdd(List<User> returnedUsers){
        Log.w(TAG, "Got list of updated " + returnedUsers.size() + " monitored users! See logcat.");
        Log.w(TAG, "All Users:");
        for (User user : returnedUsers) {
            Log.w(TAG, "    User: " + user.toString());
        }
        finish();

    }

    private void setupCancelButton() {
        Button cancel = findViewById(R.id.btnCancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(AddPersonToMonitoringListsActivity.this, "You clicked 'CANCEL'", Toast.LENGTH_SHORT)
                        .show();
                finish();
            }
        });
    }
}