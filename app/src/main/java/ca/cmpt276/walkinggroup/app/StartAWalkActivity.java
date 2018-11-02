package ca.cmpt276.walkinggroup.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.AllGroups;
import ca.cmpt276.walkinggroup.dataobjects.Theme;
import ca.cmpt276.walkinggroup.dataobjects.Group;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.model.State;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

/**
 *Activity to start a walk. choose from either the groups you lead or the groups you are in. Click the
 * group to start the walk
 */

public class StartAWalkActivity extends AppCompatActivity {
    private static final String TAG = "StartAWalkActivity";
    public static final int REQUEST_CODE_GET_POINTS = 1097;

    private State state = State.getInstance();
    private WGServerProxy proxy = state.getProxy();
    private User user = state.getUser();
    private AllGroups allGroups = AllGroups.getInstance();
    private List<Group> listAllGroups = new ArrayList<>();
    private List<Group> groupIn = new ArrayList<>();
    private List<Group> groupLead = new ArrayList<>();


    List<String> GroupsIAmInString = new ArrayList<>();
    List<String> GroupsILeadString = new ArrayList<>();

    private Integer currentPoints;
    private Integer currentTotalPoints;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Theme.setColourScheme(this);
        setContentView(R.layout.activity_start_awalk);

        setupAllGroups();
        registerLeadGroupClick();
        registerInGroupClick();
        getUserInfo(user.getId());

    }

    private void getUserInfo(Long userId) {
        Call<User> caller = proxy.getUserById(userId);
        ProxyBuilder.callProxy(StartAWalkActivity.this, caller, responseUser -> responseUser(responseUser));
    }

    private void responseUser(User responseUser) {
        user = responseUser;
        currentPoints = user.getCurrentPoints();
        currentTotalPoints = user.getTotalPointsEarned();

    }

    private void setupAllGroups() {
        Call<List<Group>> caller = proxy.getGroups();
        ProxyBuilder.callProxy(StartAWalkActivity.this, caller, returnedListOfGroups -> responseToListOfGroups(returnedListOfGroups));
    }
    private void responseToListOfGroups(List<Group> list){
        listAllGroups = list;
        populateGroupIn();
        populateGroupLead();
    }

    private void populateListViewLead(List<String> stringList) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(StartAWalkActivity.this, R.layout.groups_lead_items, stringList);
        ListView listView = findViewById(R.id.listGroupLeadWalk);
        listView.setAdapter(adapter);
    }

    private void populateListViewIn(List<String> stringList) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(StartAWalkActivity.this, R.layout.groups_i_am_in_items, stringList);
        ListView listView = findViewById(R.id.listGroupInWalk);
        listView.setAdapter(adapter);
    }

    private void populateGroupIn() {
        for(int i =0 ; i< listAllGroups.size(); i++){
            Group group = new Group();
            group = listAllGroups.get(i);
            if(group.getMemberUsers().contains(user)){
                groupIn.add(group);
                GroupsIAmInString.add(""+ group.getId()+ " - " + group.getGroupDescription());
            }

        }
        Log.d(TAG, "populateGroupIn: check size of group in: " + groupIn.size());

        populateListViewIn(GroupsIAmInString);

    }

    private void populateGroupLead() {
        for(int i =0 ; i< listAllGroups.size(); i++){
            if(listAllGroups.get(i).getLeader().getId().equals(user.getId())){
                Group group = new Group();
                group = listAllGroups.get(i);
                groupLead.add(group);
                GroupsILeadString.add(""+ group.getId()+ " - " + group.getGroupDescription());
            }

        }
        Log.d(TAG, "populateGroupLead: group I lead size: " + groupLead.size());
        populateListViewLead(GroupsILeadString);

    }


    ////////////////// Register Clicks ////////////////////////////


    private void registerLeadGroupClick(){
        ListView groupsILead = findViewById(R.id.listGroupLeadWalk);
        groupsILead.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Double groupLat = groupLead.get(position).getRouteLatArray()[1];
                Double groupLng = groupLead.get(position).getRouteLngArray()[1];
                Intent intentLead = WalkActivity.makeWalkActivityIntent(StartAWalkActivity.this,groupLat,groupLng );
                startActivityForResult(intentLead, REQUEST_CODE_GET_POINTS);

            }
        });
    }

    private void registerInGroupClick(){
        ListView groupsIAmIn = findViewById(R.id.listGroupInWalk);
        groupsIAmIn.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Double groupLat = groupIn.get(position).getRouteLatArray()[1];
                Double groupLng = groupIn.get(position).getRouteLngArray()[1];
                Intent intent = WalkActivity.makeWalkActivityIntent(StartAWalkActivity.this,groupLat,groupLng );
                startActivityForResult(intent, REQUEST_CODE_GET_POINTS);

            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case REQUEST_CODE_GET_POINTS:
                if ( resultCode == Activity.RESULT_OK){
                    //Get the points
                    int awardPoints = data.getIntExtra("walkPoints", 0);
                    currentPoints = currentPoints + awardPoints;
                    currentTotalPoints = currentTotalPoints + awardPoints;
                    user.setCurrentPoints(currentPoints);
                    user.setTotalPointsEarned(currentTotalPoints);

                    updateUserPoints();

                }
        }
    }

    private void updateUserPoints() {
        Call<User> caller = proxy.editUser(user.getId(), user);
        ProxyBuilder.callProxy(this, caller, returnNothing -> responseUpdateUser(returnNothing));
    }

    private void responseUpdateUser(User returnNothing) {
        //do nothing
        Toast.makeText(this, "Current Points: " + user.getCurrentPoints() + " Total Points: " + user.getTotalPointsEarned() , Toast.LENGTH_SHORT).show();
    }

    ////////////////// Intent ///////////////////////////


    public static Intent makeStartAWalkIntent(Context context) {
        return new Intent(context, StartAWalkActivity.class);
    }
}




