package ca.cmpt276.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import ca.cmpt276.walkinggroup.dataobjects.Theme;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.model.State;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

/**
 * Displays information of group member's parent so that they can be contacted whenever there is a problem or planning events
 */
public class GroupMembersMonitoredByInfoActivity extends AppCompatActivity {

    private static final String EXTRA_USERID = "ca.cmpt276.walkinggroup.app, GroupMembersMonitoredByInfoActivity  - monitored by user ID";

    private User monitorsMemberUser;
    State state = State.getInstance();
    WGServerProxy proxy = state.getProxy();

    Long userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Theme.setColourScheme(this);
        setContentView(R.layout.activity_group_members_monitored_by_info);

        monitorsMemberUser = new User();

        extractDataFromIntent();

    }

    public static Intent makeMembersMonitoredByInfo(Context context, Long userID) {
        Intent intent = new Intent(context, GroupMembersMonitoredByInfoActivity.class);
        intent.putExtra(EXTRA_USERID, userID);
        return intent;
    }

    private void extractDataFromIntent() {

        Intent intent = getIntent();
        userID = intent.getLongExtra(EXTRA_USERID, 0);
        getMonitoredByUserSelected();
    }

    private void getMonitoredByUserSelected() {
        Call<User> caller = proxy.getUserById(userID);
        ProxyBuilder.callProxy(GroupMembersMonitoredByInfoActivity.this, caller, responseUser -> responseMonitoredByUser(responseUser));
    }

    private void responseMonitoredByUser(User responseUser) {
        monitorsMemberUser = responseUser;
        updateUI();

    }

    private void updateUI() {
        TextView titleMonitorInfo = findViewById(R.id.txtTitleMemberMonitoredByUserInfo);
        titleMonitorInfo.setText(getString(R.string.title_monitor_information,monitorsMemberUser.getName()));

        TextView monitorName = findViewById(R.id.txt_Name_Monitor_Info);
        monitorName.setText(monitorsMemberUser.getName());

        TextView monitorEmail = findViewById(R.id.txt_Email_Monitor_Info);
        monitorEmail.setText(monitorsMemberUser.getEmail());

        TextView monitorAddress = findViewById(R.id.txt_Address_Monitor_Info);
        monitorAddress.setText(monitorsMemberUser.getAddress());

        TextView monitorHomeNumber = findViewById(R.id.txt_Home_Phone_Monitor_Info);
        monitorHomeNumber.setText(monitorsMemberUser.getHomePhone());

        TextView monitorCellNumber = findViewById(R.id.txt_Cell_Phone_Monitor_Info);
        monitorCellNumber.setText(monitorsMemberUser.getCellPhone());

        TextView monitorCurrentGrade = findViewById(R.id.txt_CurrentGrade_Monitor_Info);
        monitorCurrentGrade.setText(monitorsMemberUser.getGrade());

        TextView monitorTeacherName = findViewById(R.id.txt_Teacher_Name_Monitor_Info);
        monitorTeacherName.setText(monitorsMemberUser.getTeacherName());

        TextView monitorEmergencyContactInfo = findViewById(R.id.txt_Emergency_Contact_Monitor_Info);
        monitorEmergencyContactInfo.setText(monitorsMemberUser.getEmergencyContactInfo());

    }
}
