package ca.cmpt276.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

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
 * Shows the groups the users leads via a listview
 */

public class GroupsILeadActivity extends AppCompatActivity {

    private State state = State.getInstance();
    private WGServerProxy proxy = state.getProxy();
    private User user;
    private List<Group> leadGroupList;

    String[] groupsILead;
    Long [] groupIDs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Theme.setColourScheme(this);
        setContentView(R.layout.activity_groups_ilead);

        user= state.getUser();

        getListOfGroups();
        setupCreateGroupButton();
        setupBackButton();
        registerOnItemClick();

    }

    @Override
    protected void onResume() {
        super.onResume();
        leadGroupList = new ArrayList<Group>();
        getListOfGroups();
    }

    private void getListOfGroups(){
        Call<List<Group>> caller = proxy.getGroups();
        ProxyBuilder.callProxy(GroupsILeadActivity.this, caller, returnedListOfGroups -> responseListOfGroupsILead(returnedListOfGroups));
    }

    private void responseListOfGroupsILead(List<Group> groupList){
        leadGroupList = new ArrayList<Group>();

        for( int i =0 ; i < groupList.size(); i++){
            if(  (long) groupList.get(i).getLeader().getId() ==  user.getId()){
                leadGroupList.add(groupList.get(i));
            }
        }

        groupsILead = new String[leadGroupList.size()];
        groupIDs = new Long [leadGroupList.size()];

        for (int j = 0; j< leadGroupList.size(); j++){
            groupsILead[j] = leadGroupList.get(j).getGroupDescription() + " - (" + leadGroupList.get(j).getId() +")" ;
            groupIDs[j] = leadGroupList.get(j).getId();
        }
        populateListView(groupsILead);

    }

    private void populateListView(String [] groupsILead ) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                GroupsILeadActivity.this,
                R.layout.groups_lead_items,
                groupsILead);

        ListView listView = findViewById(R.id.listGroupLead);
        listView.setAdapter(adapter);
    }


    private void setupCreateGroupButton(){

        Button Create = (Button)findViewById(R.id.btnCreateGroup) ;
        Create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createGroup = CreateGroupActivity.makeCreateGroupIntent(GroupsILeadActivity.this);
                startActivity(createGroup);
            }
        });
    }

    private void setupBackButton() {
        Button back = (Button) findViewById(R.id.btnBackMenulead);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void registerOnItemClick(){

        ListView groupsILead = findViewById(R.id.listGroupLead);

        groupsILead.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent membersInGroupILeadIntent = MembersInGroupActivity.makeMembersInGroupIntent(GroupsILeadActivity.this,
                        groupIDs[position],true);
                startActivity(membersInGroupILeadIntent);

            }
        });
    }






    public static Intent makeGroupsILeadIntent(Context context) {
        return new Intent(context, GroupsILeadActivity.class);
    }




}

