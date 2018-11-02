package ca.cmpt276.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.Theme;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.model.State;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

/**
 * Displays a listView of of parents of the member chosen from the group
 * Click parent to see in-depth contact info for them
 */
public class GroupMembersMonitoredByActivity extends AppCompatActivity {
    private static final String EXTRA_USERID = "ca.cmpt276.walkinggroup.app, GroupMembersMonitoredByActivity - member of group user ID";

    State state = State.getInstance();
    WGServerProxy proxy = state.getProxy();

    private Long userID;
    private ListView memberMonitoredByList;

    String [] monitoredByList;
    Long[] monitoredByUserIDs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Theme.setColourScheme(this);

        setContentView(R.layout.activity_groups_members_monitored_by);

        extractDataFromIntent();
        registerCallBack();
    }

    public static Intent makeMembersMonitoredByIntent(Context context, Long userID) {
        Intent intent = new Intent(context, GroupMembersMonitoredByActivity.class);
        intent.putExtra(EXTRA_USERID, userID);
        return intent;
    }

    private void extractDataFromIntent() {

        Intent intent = getIntent();
        userID = intent.getLongExtra(EXTRA_USERID, 0);
        getMemberSelected();

    }

    private void getMemberSelected() {
        Call<User> caller = proxy.getUserById(userID);
        ProxyBuilder.callProxy(GroupMembersMonitoredByActivity.this, caller, responseUser -> responseMemberMonitoredBy(responseUser));
    }

    private void responseMemberMonitoredBy(User responseUser) {

        TextView txtTitleMemberMonitoredBy = findViewById(R.id.txtTitleMembersInGroups);
        txtTitleMemberMonitoredBy.setText( getString(R.string.title_member_monitored_by_groups_i_am_in, responseUser.getName()));

//        TextView txtEmergencyMemberInfo = findViewById(R.id.txt_Emergency_Info_Member);
//        if (responseUser.getEmergencyContactInfo()!=null) {
//            txtEmergencyMemberInfo.setText(String.format(getString(R.string.emergency_member_contact_info), responseUser.getEmergencyContactInfo()));
//        }

        Call<List<User>> caller = proxy.getMonitoredByUsers(userID);
        ProxyBuilder.callProxy(GroupMembersMonitoredByActivity.this, caller, responseMonitorsMemberList -> responseMonitorsMemberList(responseMonitorsMemberList));
    }

    private void responseMonitorsMemberList(List<User> responseMonitorsMemberList) {

        monitoredByList = new String[responseMonitorsMemberList.size()];
        monitoredByUserIDs = new Long[responseMonitorsMemberList.size()];

        for(int i =0; i<responseMonitorsMemberList.size(); i++){
            User user = responseMonitorsMemberList.get(i);
            monitoredByList[i]= user.getName() + " -  " + "( "+ user.getEmail() + " )";
            monitoredByUserIDs[i]=user.getId();
        }
        populateListView(monitoredByList);
    }

    private void populateListView(String[] monitoredByList) {
        //Build Array Adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                R.layout.member_monitored_by_items,
                monitoredByList);

        // Configure the list view
        ListView listView = findViewById(R.id.list_Member_Monitored_By);
        listView.setAdapter(adapter);
    }
    private void registerCallBack() {
        memberMonitoredByList = findViewById(R.id.list_Member_Monitored_By);
        setupClickMonitoredByInfo();
    }

    private void setupClickMonitoredByInfo() {
        memberMonitoredByList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent monitoredByInfo = GroupMembersMonitoredByInfoActivity.makeMembersMonitoredByInfo(GroupMembersMonitoredByActivity.this,
                        monitoredByUserIDs[position]);
                startActivity(monitoredByInfo);
            }
        });
    }
}
