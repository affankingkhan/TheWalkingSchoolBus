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
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.Theme;
import ca.cmpt276.walkinggroup.dataobjects.Group;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.model.State;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

/**
 * Displays listView of all members in the group selected
 */

public class MembersInGroupActivity extends AppCompatActivity {

    private static final String EXTRA_GROUP_ID = "ca.cmpt276.walkinggroup.app, MembersInGroupActivity  - group ID selected";
    private static final String EXTRA_IS_GROUP_I_LEAD = "ca.cmpt276.walkinggroup.app, MembersInGroupActivity  - is Group I lead?";
    private State state = State.getInstance();
    private WGServerProxy proxy = state.getProxy();

    Long groupID;
    String groupName;
    private ListView membersInGroupList;

    Long [] UserIDs ;
    String[] groupMembers;

    boolean groupILead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Theme.setColourScheme(this);
        setContentView(R.layout.activity_members_in_group);

        extractDataFromIntent();
        getGroup();
        registerClickCallBack();
    }

    public static Intent makeMembersInGroupIntent(Context context, Long groupID, boolean isGroupILead) {
        Intent intent = new Intent(context, MembersInGroupActivity.class);
        intent.putExtra(EXTRA_GROUP_ID, groupID);
        intent.putExtra(EXTRA_IS_GROUP_I_LEAD, isGroupILead);
        return intent;
    }

    private void extractDataFromIntent() {

        Intent intent = getIntent();
        groupID = intent.getLongExtra(EXTRA_GROUP_ID, 0);
        groupILead = intent.getBooleanExtra(EXTRA_IS_GROUP_I_LEAD, false);
        getMembersOfGroupSelected();

    }

    private void getMembersOfGroupSelected() {
        Call<List<User>> caller = proxy.getGroupMembers(groupID);
        ProxyBuilder.callProxy(MembersInGroupActivity.this, caller, responseListMembers -> responseMembersInGroup(responseListMembers));
    }

    private void responseMembersInGroup(List<User> responseListMembers) {


        groupMembers = new String[responseListMembers.size()];
        UserIDs = new Long[responseListMembers.size()];

        for(int i =0; i< responseListMembers.size(); i++){
            User user = responseListMembers.get(i);
            groupMembers[i]= user.getName() + " -  " + "( "+ user.getEmail() + " )";
            UserIDs[i]=user.getId();
        }
        populateListView(groupMembers);
    }



    private void populateListView(String[] groupMembers) {
        //Build Array Adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                R.layout.members_in_groups,
                groupMembers);

        // Configure the list view
        ListView listView = findViewById(R.id.members_in_group_list);
        listView.setAdapter(adapter);
    }
    private void registerClickCallBack() {
        membersInGroupList = findViewById(R.id.members_in_group_list);
        setupClickMemberInGroup();
        if(groupILead){
            TextView changeInstructions = findViewById(R.id.txt_Instructions_Members_In_Group);
            changeInstructions.setText(R.string.instructions_extra_change);
            setupLongClickDeleteFromGroupILead();
        }
    }
    private void setupLongClickDeleteFromGroupILead() {
        membersInGroupList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {


                AlertDialog.Builder builder = new AlertDialog.Builder(MembersInGroupActivity.this);

                builder.setCancelable(true);
                builder.setTitle(R.string.alerttitle);
                builder.setMessage(getString(R.string.alertMessage_delete_member_groups_i_lead, groupMembers[position], groupName));
                builder.setPositiveButton(R.string.alertbutton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteFromGroup(position);
                    }
                });

                builder.setNegativeButton(R.string.cancelbutton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.show();


                return true;
            }
        });
    }

    private void deleteFromGroup(int position) {

        Call<Void> caller = proxy.removeGroupMember(groupID, UserIDs[position]);
        ProxyBuilder.callProxy(this,caller,nothing ->updateList(nothing));
    }

    private void updateList(Void nothing) {

        getMembersOfGroupSelected();
    }


    private void setupClickMemberInGroup() {
        membersInGroupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent membersMonitoredByIntent = GroupMembersMonitoredByActivity.makeMembersMonitoredByIntent(MembersInGroupActivity.this,
                        UserIDs[position]);
                startActivity(membersMonitoredByIntent);
            }
        });

    }

    private void getGroup() {
        Call<Group> caller = proxy.getGroupById(groupID);
        ProxyBuilder.callProxy(this, caller, returnedGroup -> respondGroupDetails(returnedGroup));
    }

    private void respondGroupDetails(Group returnedGroup) {
        groupName = returnedGroup.getGroupDescription();
        TextView titleMembersInGroup = findViewById(R.id.txtTitleMembersInGroupsIAmIn);
        titleMembersInGroup.setText(getString(R.string.title_members_in_groups_I_am_in, groupName));
    }

}