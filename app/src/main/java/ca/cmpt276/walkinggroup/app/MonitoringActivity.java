package ca.cmpt276.walkinggroup.app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.Theme;
import ca.cmpt276.walkinggroup.model.State;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

/**
 * Displays a listView displaying all users who I monitor or users who monitor me.
 * Single click leads to AddMonitorOthersActivity
 * Long click able to remove a person who I monitor with alert dialog
 */

public class MonitoringActivity extends AppCompatActivity {

    private static final String EXTRA_IS_USER_MONITOR = "ca.cmpt276.walkinggroup.app, MonitoringActivity  - is User I Monitor?";

    State state = State.getInstance();
    WGServerProxy proxy = state.getProxy();
    Long currentUserId = state.getUser().getId();

    Long [] UserIDs ;
    String[] monitorPeople;
    boolean usersIMonitor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Theme.setColourScheme(this);
        setContentView(R.layout.activity_monitoring);

        extractDataFromIntent();
        setupAddToMonitorUserButton();
        registerLongClickDelete();
        setUpBackToMainMenuButton();

        if(usersIMonitor) {
            registerClickOnMonitoringPersonInfo();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getListOfMonitoring();

    }

    public static Intent makeMonitoringIntent(Context context, boolean isUserIMonitor) {
        Intent intent = new Intent(context, MonitoringActivity.class);
        intent.putExtra(EXTRA_IS_USER_MONITOR, isUserIMonitor);
        return intent;
    }

    private void extractDataFromIntent() {

        Intent intent = getIntent();
        usersIMonitor = intent.getBooleanExtra(EXTRA_IS_USER_MONITOR, false);
        getListOfMonitoring();
        updateUI();
    }

    private void updateUI() {
            Button btnAddUser = findViewById(R.id.btnAddMonitorUser);
            TextView title = findViewById(R.id.txtTitleOfMonitor);
            TextView instructions = findViewById(R.id.txtMonitorInstructions);
            if(usersIMonitor){
                btnAddUser.setText(R.string.btn_add_user_to_monitor);
                title.setText(R.string.title_i_monitor);
                instructions.setText(R.string.delete_instructions);
            }
            else{
                btnAddUser.setText(R.string.add_a_user_to_monitor_me);
                title.setText(R.string.list_of_users_who_monitor_me);
                instructions.setText(R.string.long_press_on_user_you_want_to_remove_from_monitoring_you);
            }
    }

    private void getListOfMonitoring(){

        if(usersIMonitor) {
            Call<List<User>> caller = proxy.getMonitorsUsers(currentUserId);
            ProxyBuilder.callProxy(MonitoringActivity.this, caller,
                    responseListOfUsersIMonitor -> responseList(responseListOfUsersIMonitor));
        }
        else{
            Call<List<User>> caller = proxy.getMonitoredByUsers(currentUserId);
            ProxyBuilder.callProxy(MonitoringActivity.this, caller, responseListOfUsersMonitoringMe ->
                    responseList(responseListOfUsersMonitoringMe));
        }

    }

    private void responseList(List<User> responseListOfUsers) {

        monitorPeople = new String[responseListOfUsers.size()];
        UserIDs = new Long[responseListOfUsers.size()];

        for (int i = 0; i < responseListOfUsers.size(); i++){
            User user = responseListOfUsers.get(i);
            monitorPeople[i]= user.getName() + " -  " + "( "+ user.getEmail() + " )";
            UserIDs[i]=user.getId();
        }
        populateListView(monitorPeople);
    }


    private void populateListView(String[] monitorPeople) {

        //Build Array Adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                R.layout.monitoring_items,
                monitorPeople);

        // Configure the list view
        ListView listView = findViewById(R.id.listMonitoringUsers);
        listView.setAdapter(adapter);
    }



    private void setupAddToMonitorUserButton() {
        Button addUser = findViewById(R.id.btnAddMonitorUser);
        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addMonitorUserIntent;
                if (usersIMonitor) {
                    addMonitorUserIntent = AddPersonToMonitoringListsActivity.AddToMonitoringIntent(
                            MonitoringActivity.this, true);
                }
                else{
                    addMonitorUserIntent = AddPersonToMonitoringListsActivity.AddToMonitoringIntent(
                            MonitoringActivity.this, false);
                }
                startActivity(addMonitorUserIntent);

            }
        });
    }




    private void registerLongClickDelete(){

        ListView monitorList= findViewById(R.id.listMonitoringUsers);

        monitorList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MonitoringActivity.this)
                .setCancelable(true)
                .setTitle(R.string.alerttitle);
                if(usersIMonitor) {
                    builder.setMessage(getString(R.string.alertmessage, monitorPeople[position]));
                }
                else{
                    builder.setMessage(getString(R.string.alert_dialog_message, monitorPeople[position]));
                }

                //Remove button
                builder.setPositiveButton(R.string.alertbutton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteMonitorPeople(position);
                    }
                });

                //Cancel button
                builder.setNegativeButton(R.string.cancelbutton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
                return true;
            }
        });
    }

    private void deleteMonitorPeople(int position) {

        if(usersIMonitor) {
            Call<Void> caller = proxy.removeFromMonitorsUsers(currentUserId, UserIDs[position]);
            ProxyBuilder.callProxy(this, caller, nothing -> updateListView(nothing));
        }
        else{
            Call<Void> caller = proxy.removeFromMonitoredByUsers(currentUserId, UserIDs[position]);
            ProxyBuilder.callProxy(this, caller, nothing -> updateListView(nothing));
        }

    }

    private void updateListView(Void nothing) {

        Toast.makeText(this, R.string.success_remove_monitoring_others, Toast.LENGTH_SHORT).show();
        getListOfMonitoring();

    }

    private void setUpBackToMainMenuButton() {
        Button btnMainMenu = findViewById(R.id.btnBackMainMenu);
        btnMainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void registerClickOnMonitoringPersonInfo(){

        ListView monitorList= findViewById(R.id.listMonitoringUsers);

        monitorList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent childInfoIntent = EditInformationActivity.makeInfoIntent(MonitoringActivity.this,
                        UserIDs[position], false);
                startActivity(childInfoIntent);

            }
        });


    }
}
