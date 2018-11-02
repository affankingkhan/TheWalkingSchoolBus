package ca.cmpt276.walkinggroup.app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.Theme;
import ca.cmpt276.walkinggroup.dataobjects.Group;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.model.State;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

/**
 *  Able to add person you monitor(child) to a group by long press on group and an alert dialog pops up
 */

public class AddMonitoringToGroupActivity extends AppCompatActivity {

    private static final String EXTRA_USERID = "ca.cmpt276.walkinggroup.app, AddMonitoringToGroupActivity - current user ID";
    private static final String TAG = "Add Monitoring Group";

    State state = State.getInstance();
    WGServerProxy proxy = state.getProxy();

    private Long userId;

    Long [] groupsIDs;
    String[] allGroups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Theme.setColourScheme(this);
        setContentView(R.layout.activity_add_monitoring_to_group);

        getListOfAllGroups();
        extractDataFromIntent();

    }

    public static Intent makeAddToGroup(Context context, Long UserId) {
        Intent intent = new Intent(context, AddMonitoringToGroupActivity.class);
        intent.putExtra(EXTRA_USERID, UserId);
        return intent;
    }

    private void extractDataFromIntent() {
        Intent intent = getIntent();
        userId = intent.getLongExtra(EXTRA_USERID, 0);
        getUserSelected(userId);

    }
    private void getUserSelected(Long userId) {
        Call<User> caller = proxy.getUserById(userId);
        ProxyBuilder.callProxy(AddMonitoringToGroupActivity.this, caller, responseUser ->
                setUpAddLongClickListener(responseUser));

    }

    private void getListOfAllGroups(){
        Call<List<Group>> caller = proxy.getGroups();
        ProxyBuilder.callProxy(AddMonitoringToGroupActivity.this, caller,
                responseDisplayAll -> responseList(responseDisplayAll));
    }

    private void responseList(List<Group> responseDisplayAll) {

        allGroups = new String[responseDisplayAll.size()];
        groupsIDs = new Long[responseDisplayAll.size()];

        for (int i = 0; i < responseDisplayAll.size(); i++){
            Group group = responseDisplayAll.get(i);
            allGroups[i]= group.getGroupDescription() + " - " + "(" + group.getId() + ")";
            groupsIDs[i] = group.getId();

        }
        populateListView(allGroups);
    }

    private void populateListView(String [] allGroups) {

        //Build Array Adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                R.layout.all_groups_items,
                allGroups);

        // Configure the list view
        ListView listView = findViewById(R.id.listView_Display_All_Groups);
        listView.setAdapter(adapter);
    }


    private void setUpAddLongClickListener(User user) {
        ListView allGroupList = findViewById(R.id.listView_Display_All_Groups);

        allGroupList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddMonitoringToGroupActivity.this)
                        .setCancelable(true)
                        .setTitle(R.string.alert_add_person_to_group)
                        //TODO: ADD user name in message
                        .setMessage(getString(R.string.alert_add_person_message,allGroups[position]))
                        .setPositiveButton(R.string.alert_add_to_group, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                addPersonToGroup(user, position);

                            }
                        })
                        .setNegativeButton(R.string.cancel_add_monitoring_to_group, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.cancel();
                            }
                        });
                builder.show();
                return false;
            }
        });
    }


    private void addPersonToGroup(User user, int position) {
        Call<List<User>> caller = proxy.addGroupMember(groupsIDs[position], user);
        ProxyBuilder.callProxy(AddMonitoringToGroupActivity.this, caller, allGroupMembers ->
                responseUpdateGroupMembers(allGroupMembers));
    }


    private void responseUpdateGroupMembers(List<User> allGroupMembers) {
        Toast.makeText(this, getString(R.string.add_person_to_group_toast) , Toast.LENGTH_SHORT)
                .show();

        Log.w(TAG, "Got list of " + allGroupMembers.size() + " users! See logcat.");
        Log.w(TAG, "All Users:");
        for (User user : allGroupMembers) {
            Log.w(TAG, "    User: " + user.toString());
        }

//        Intent groupsMonitoringIntent = GroupsMonitoringUserIsInActivity.makeGroupsInIntent(AddMonitoringToGroupActivity.this,
//                userId);
//        startActivity(groupsMonitoringIntent);
        finish();
    }

}
