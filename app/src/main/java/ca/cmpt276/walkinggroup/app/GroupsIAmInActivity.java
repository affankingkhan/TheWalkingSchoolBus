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
 * shows the groups the user is in via a listview
 */

public class GroupsIAmInActivity extends AppCompatActivity {

    private static final String TAG = "GroupsIAmInActivity";


    private State state = State.getInstance();
    private WGServerProxy proxy = state.getProxy();
    private User user = state.getUser();
    private ListView groupsIn;

    String[] groupsIAmIn;
    Long [] groupIDs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Theme.setColourScheme(this);
        setContentView(R.layout.activity_groups_iam_in);

        setupBackButton();
        getListOfGroupIAmIn();
        registerClickCallBack();
    }

    private void getListOfGroupIAmIn(){
        Call<List<Group>> caller = proxy.getGroups();
        ProxyBuilder.callProxy(GroupsIAmInActivity.this, caller, returnedListOfGroups -> responseListOfGroups(returnedListOfGroups));

    }

    private void responseListOfGroups(List<Group> list){
        List<Group> groupIn = new ArrayList<Group>();

        for (int i = 0; i<list.size(); i++){
            Group group = list.get(i);
            if(group.getMemberUsers().contains(user)) {
                groupIn.add(group);
            }
        }

        groupsIAmIn = new String[groupIn.size()];
        groupIDs = new Long[groupIn.size()];

        for (int i = 0; i < groupIn.size(); i++) {
            Group group = groupIn.get(i);
            groupsIAmIn[i] =group.getId() +"-" + group.getGroupDescription();
            groupsIAmIn[i] =group.getGroupDescription()+ " - (" +group.getId() +")" ;
            groupIDs[i]= group.getId();
        }
        populateListView(groupsIAmIn);
    }

    private void populateListView(String[] groupsIAmIn){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                GroupsIAmInActivity.this,
                R.layout.groups_i_am_in_items,
                groupsIAmIn);

        ListView listView = findViewById(R.id.listGroupIn);
        listView.setAdapter(adapter);
    }
    private void registerClickCallBack() {
        groupsIn= findViewById(R.id.listGroupIn);
        setupLongClickDelete();
        setupClickMembersInGroup();
    }


    private void setupLongClickDelete() {

        groupsIn.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {


                AlertDialog.Builder builder = new AlertDialog.Builder(GroupsIAmInActivity.this);

                builder.setCancelable(true);
                builder.setTitle(R.string.groups_I_am_in_title);
                builder.setMessage(getString(R.string.alertIAmInMessage, groupsIAmIn[position]));
                builder.setPositiveButton(R.string.alertbutton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        removeFromGroup(position);

                    }
                });
                builder.setNegativeButton(R.string.cancelbutton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();

                    }
                });

                builder.show();
                return true;
            }
        });
    }


    private void removeFromGroup(int position) {

        Call<Void> caller = proxy.removeGroupMember(groupIDs[position],user.getId());
        ProxyBuilder.callProxy(this,caller,nothing ->updateList(nothing));
    }


    private void updateList(Void nothing) {
        getListOfGroupIAmIn();
    }

    private void setupClickMembersInGroup() {
        groupsIn.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent membersInGroupIamInIntent = MembersInGroupActivity.makeMembersInGroupIntent(GroupsIAmInActivity.this,
                        groupIDs[position], false);
                startActivity(membersInGroupIamInIntent);
            }
        });
    }



    private void setupBackButton() {
        Button back = (Button) findViewById(R.id.btnBackMenuGroupIn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    public static Intent makeGroupsIAmInIntent(Context context) {
        return new Intent(context, GroupsIAmInActivity.class);
    }

}
