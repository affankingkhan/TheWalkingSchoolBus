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

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.Theme;
import ca.cmpt276.walkinggroup.dataobjects.Group;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.model.State;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

/**
 * Displays in listView the groups the user's child is in
 */
public class GroupsMonitoringUserIsInActivity extends AppCompatActivity {

    private static final String EXTRA_USER_ID = "ca.cmpt276.walkinggroup.app, GroupsMonitoringUserIsInActivity - monitoring user ID";

    State state = State.getInstance();
    WGServerProxy proxy = state.getProxy();

    private Long userId;
    private User childUser;
    private ListView userGroupList;

    Long [] groupIDs;
    String[] userGroups;

    private List<Group> userInGroup = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Theme.setColourScheme(this);
        setContentView(R.layout.activity_groups_monitoring_user_is_in);

        childUser = new User();

        extractDataFromIntent();
        registerClickCallBack();
        setUpAddUserToGroupButton();
    }


    @Override
    protected void onResume() {
        super.onResume();
        userInGroup = new ArrayList<Group>();
        getUserSelected();
    }

    public static Intent makeGroupsInIntent(Context context, Long userId) {
        Intent intent = new Intent(context, GroupsMonitoringUserIsInActivity.class);
        intent.putExtra(EXTRA_USER_ID, userId);
        return intent;
    }

    private void extractDataFromIntent() {
        Intent intent = getIntent();
        userId = intent.getLongExtra(EXTRA_USER_ID, 0);
        getUserSelected();
    }


    private void getUserSelected() {
        Call<User> caller = proxy.getUserById(userId);
        ProxyBuilder.callProxy(GroupsMonitoringUserIsInActivity.this, caller, responseUser ->
                response(responseUser));

    }

    void response(User returnedUser) {
        childUser = returnedUser;
        //Update screen with Child's name
        TextView txtTitleGroupsChildIn = findViewById(R.id.txtTitleGroupsIn);
        txtTitleGroupsChildIn.setText(getString(R.string.title_groups_child_in, childUser.getName()));

        Button btnAddChildToGroup = findViewById(R.id.btnAddToGroup);
        btnAddChildToGroup.setText(getString(R.string.add_user_to_groups, childUser.getName()));

        getListOfGroups();

    }

    private void getListOfGroups() {
        Call<List<Group>> caller = proxy.getGroups();
        ProxyBuilder.callProxy(GroupsMonitoringUserIsInActivity.this, caller, returnedListOfGroups -> responseListOfGroupsMonitoringUserIn(returnedListOfGroups));
    }

    private void responseListOfGroupsMonitoringUserIn(List<Group> returnedListOfGroups) {
        List<Group> userInGroup = new ArrayList<>();

        for (int i = 0; i<returnedListOfGroups.size(); i++){
            Group group = returnedListOfGroups.get(i);
            if(group.getMemberUsers().contains(childUser)) {
                userInGroup.add(group);
            }
        }


        userGroups = new String[userInGroup.size()];
        groupIDs = new Long[userInGroup.size()];

        for (int i = 0; i < userInGroup.size(); i++) {
            Group group = userInGroup.get(i);
            userGroups[i] =group.getGroupDescription() + " - (" + group.getId() +")";
            groupIDs[i]= group.getId();
        }
        populateListView(userGroups);
    }



    private void populateListView(String[] userGroups) {

        //Build Array Adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                R.layout.users_groups_items,
                userGroups);

        // Configure the list view
        ListView listView = findViewById(R.id.list_of_groups_user_in);
        listView.setAdapter(adapter);
    }

    private void setUpAddUserToGroupButton() {
        Button btnAddUser = findViewById(R.id.btnAddToGroup);
        btnAddUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addMonitorToGroupIntent = AddMonitoringToGroupActivity.makeAddToGroup(GroupsMonitoringUserIsInActivity.this,
                        userId );
                startActivity(addMonitorToGroupIntent);
            }
        });
    }

    private void registerClickCallBack() {
        userGroupList = findViewById(R.id.list_of_groups_user_in);
        setupDeleteLongClickListener();
        setupClickForMembersInGroup();

    }

    private void setupClickForMembersInGroup() {
        userGroupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO: CHANGE NAME IF WANT TO USE LIKE THIS
                Intent membersInGroupIamInIntent = MembersInGroupActivity.makeMembersInGroupIntent(GroupsMonitoringUserIsInActivity.this,
                        groupIDs[position],false);
                startActivity(membersInGroupIamInIntent);
            }
        });

    }

    private void setupDeleteLongClickListener() {

        userGroupList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(GroupsMonitoringUserIsInActivity.this)
                        .setCancelable(true)
                        .setTitle(getString(R.string.alert_remove_user_from_group, childUser.getName()))
                        .setMessage(getString(R.string.alert_remove_user_from_group_message,childUser.getName(),userGroups[position]))
                        .setPositiveButton(R.string.alert_remove_from_group, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                deletePersonFromGroup(position);

                            }
                        })
                        .setNegativeButton(R.string.cancel_add_monitoring_to_group, new DialogInterface.OnClickListener() {
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

    private void deletePersonFromGroup(int position) {
        Call<Void> caller = proxy.removeGroupMember(groupIDs[position], userId);
        ProxyBuilder.callProxy(GroupsMonitoringUserIsInActivity.this, caller, nothing -> updateListView(nothing) );
    }

    private void updateListView(Void nothing) {
        getUserSelected();
        Toast.makeText(this, R.string.deleted_person_from_group_toast, Toast.LENGTH_SHORT)
                .show();
    }


}
