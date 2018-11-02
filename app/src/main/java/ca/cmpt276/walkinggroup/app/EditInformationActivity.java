package ca.cmpt276.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.Theme;
import ca.cmpt276.walkinggroup.dataobjects.EarnedRewards;
import ca.cmpt276.walkinggroup.dataobjects.Store;
import ca.cmpt276.walkinggroup.dataobjects.Title;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.model.State;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

/**
 * Activity displays all of Child/Personal info which can be edited
 * Save Edit button sends to the Server all the edited information
 */
public class EditInformationActivity extends AppCompatActivity {

    private static final String EXTRA_USERID = "ca.cmpt276.walkinggroup.app, EditInformationActivity - user ID";
    private static final String EXTRA_IS_CURRENT_USER = "ca.cmpt276.walkinggroup.app, EditInformationActivity - is Current User?" ;

    private Long userId;
    private User user;
    State state = State.getInstance();
    WGServerProxy proxy = state.getProxy();
    private Store store = Store.getInstance();

    private EditText editName;
    private EditText editEmail;
    private EditText editBirthYear;
    private EditText editBirthMonth;
    private EditText editAddress;
    private EditText editHomeNumber;
    private EditText editCellPhoneNumber;
    private EditText editCurrentGrade;
    private EditText editTeacherName;
    private EditText editEmergencyContact;

    boolean isCurrentUser;

    private List<Title> titles = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Theme.setColourScheme(this);
        setContentView(R.layout.activity_edit_information);


        extractDataFromIntent();
        if(!isCurrentUser) {
            setupChildGroupListButton();
            user = new User();
        }
        else{
            user =state.getUser();
        }

        initializeAll();
        setupSaveEditButton();
        setupCancelEditButton();

    }

    public static Intent makeInfoIntent(Context context, Long userId, boolean isCurrentUser) {
        Intent intent = new Intent(context, EditInformationActivity.class );
        intent.putExtra(EXTRA_USERID, userId);
        intent.putExtra(EXTRA_IS_CURRENT_USER, isCurrentUser);
        return intent;
    }
    private void extractDataFromIntent() {
        Intent intent = getIntent();
        userId = intent.getLongExtra(EXTRA_USERID, 0);
        isCurrentUser = intent.getBooleanExtra(EXTRA_IS_CURRENT_USER, false);
        getUserInfo(userId);

    }

    private void initializeAll() {

        //Initializing editViews
        editName = findViewById(R.id.edit_Name_Info);
        editEmail = findViewById(R.id.edit_Email_Info);
        editBirthYear = findViewById(R.id.edit_Birth_Year_Info);
        editBirthMonth = findViewById(R.id.edit_Birth_Month_Info);
        editAddress = findViewById(R.id.edit_Address_Info);
        editHomeNumber = findViewById(R.id.edit_Home_Phone_Info);
        editCellPhoneNumber = findViewById(R.id.edit_Cell_Phone_Info);
        editCurrentGrade = findViewById(R.id.edit_Current_Grade_Info);
        editTeacherName = findViewById(R.id.edit_Teachers_Name_Info);
        editEmergencyContact = findViewById(R.id.edit_Emergency_Contact_Info);
    }


    private void getUserInfo(Long userId) {
        Call<User> caller = proxy.getUserById(userId);
        ProxyBuilder.callProxy(EditInformationActivity.this, caller, responseUser -> responseUser(responseUser));
    }

    private void responseUser(User responseChildUser) {
        user = responseChildUser;
//        //// THIS IS ADDITIONAL CODE FOR TESTING PURPOSE ///////////////////
//        Title title1 = new Title();
//        Title title2= new Title();
//        title1 = store.getListofTitles().get(0);
//        title2 = store.getListofTitles().get(1);
//        EarnedRewards earnedRewards= new EarnedRewards();
//        titles.add(title1);
//        titles.add(title2);
//        earnedRewards.setEarnedTitles(titles);
//        earnedRewards.setCurrentTitle(title1);
//
//        user.setRewards(earnedRewards);
        updateUI();
    }


    private void updateUI() {

        //Setting texts for screen with child's name
        TextView txtChildInfoTitle = findViewById(R.id.txtInformationTitle);
        if(isCurrentUser) {
            txtChildInfoTitle.setText(R.string.personal_information_title);
        }
        else {
            txtChildInfoTitle.setText(getString(R.string.child_info_title, user.getName()));

            Button btnChildGroupList = findViewById(R.id.btnChildGroups);
            btnChildGroupList.setVisibility(View.VISIBLE);
            btnChildGroupList.setText(getString(R.string.button_child_group_list, user.getName()));
        }

        editName.setText(user.getName());

        editEmail.setText(user.getEmail());

        if (user.getBirthYear() != null) {
            editBirthYear.setText(String.valueOf(user.getBirthYear()));
        }
        if (user.getBirthMonth() != null) {
            editBirthMonth.setText(String.valueOf(user.getBirthMonth()));
        }
        editAddress.setText(user.getAddress());

        editHomeNumber.setText(user.getHomePhone());

        editCellPhoneNumber.setText(user.getCellPhone());

        editCurrentGrade.setText(user.getGrade());

        editTeacherName.setText(user.getTeacherName());

        editEmergencyContact.setText(user.getEmergencyContactInfo());

    }

    private void setupSaveEditButton() {
        Button btnSaveEdit = findViewById(R.id.btnSave_Info);
        btnSaveEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserInfo();
            }
        });
    }

    private void setupCancelEditButton() {
        Button btnCancelEdit = findViewById(R.id.btnCancel_Info);
        btnCancelEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void updateUserInfo() {
        String updateName = editName.getText().toString();
        if (!updateName.isEmpty()) {
            user.setName(updateName);
        }
        else{
            Toast.makeText(this, "Please insert a name", Toast.LENGTH_SHORT).show();
        }

        String updateEmail= editEmail.getText().toString();
        if(!updateEmail.isEmpty()){
            user.setEmail(updateEmail);
        }
        else{
            Toast.makeText(this, "Please insert an email", Toast.LENGTH_SHORT).show();
        }

        String updateBirthYearString = editBirthYear.getText().toString();
        if (!updateBirthYearString.isEmpty()) {
            user.setBirthYear(Integer.parseInt(updateBirthYearString));
        }

        String updateBirthMonthString = editBirthMonth.getText().toString();
        if (!updateBirthMonthString.isEmpty()) {
            user.setBirthMonth(Integer.parseInt(updateBirthMonthString));
        }


        user.setAddress(editAddress.getText().toString());

        user.setHomePhone(editHomeNumber.getText().toString());

        user.setCellPhone(editCellPhoneNumber.getText().toString());

        user.setGrade(editCurrentGrade.getText().toString());

        user.setTeacherName(editTeacherName.getText().toString());

        user.setEmergencyContactInfo(editEmergencyContact.getText().toString());

        if(!updateName.isEmpty() && !updateEmail.isEmpty()) {
            Call<User> caller = proxy.editUser(user.getId(), user);
            ProxyBuilder.callProxy(this, caller, returnNothing -> responseUpdateUser(returnNothing));
        }
    }

    private void responseUpdateUser(User returnNothing) {
        finish();
    }

    private void setupChildGroupListButton() {
        Button btnGroupsChildIn = findViewById(R.id.btnChildGroups);
        btnGroupsChildIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent groupsUserInIntent = GroupsMonitoringUserIsInActivity.makeGroupsInIntent(EditInformationActivity.this,
                        userId);
                startActivity(groupsUserInIntent);

            }
        });

    }
}
