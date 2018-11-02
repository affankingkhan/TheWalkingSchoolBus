package ca.cmpt276.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.Theme;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.model.State;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class LeaderBoardActivity extends AppCompatActivity {

    private State state = State.getInstance();
    private WGServerProxy proxy = state.getProxy();

    User[] allUsers;
    String [] leaderBoardUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Theme.setColourScheme(this);
        setContentView(R.layout.activity_leader_board);

        getAllUsers();
    }

    public static Intent makeLeaderBoardIntent(Context context) {
        return new Intent(context, LeaderBoardActivity.class);
    }

    private void getAllUsers() {
        Call<List<User>> caller = proxy.getUsers();
        ProxyBuilder.callProxy(LeaderBoardActivity.this, caller, responseListUsers-> responseListUsers(responseListUsers));

    }

    private void responseListUsers(List<User> responseListUsers) {

        allUsers = new User[responseListUsers.size()];
        for(int i = 0; i<responseListUsers.size(); i++){
            User user = responseListUsers.get(i);
                allUsers[i] = user;
        }

        updateSortedUsersWithInfo();

    }

    private void updateSortedUsersWithInfo() {
        User sortedUsers[] = sortUsersByTotalPoints(allUsers);

        // For top 100 Leader Board
        int listLength=sortedUsers.length;
        if (sortedUsers.length > 100){
            listLength = 100;
        }
        leaderBoardUserInfo = new String[listLength];

        for(int j=0; j<listLength; j++){

            String fullName = sortedUsers[j].getName();
            int firstSpace = fullName.indexOf(" ");
            int lastSpace =fullName.lastIndexOf(" ");

            if(fullName.equals(" ")){
                leaderBoardUserInfo[j] = j+1 + ". "  + "(No Name)" + " -  ("
                        + sortedUsers[j].getTotalPointsEarned() +")";
            }
            else if(firstSpace!=-1) {
                String firstName = fullName.substring(0, firstSpace);
                char lastNameInitial =fullName.charAt(lastSpace +1);
                leaderBoardUserInfo[j] = j+1 + ". "  + firstName + " " + lastNameInitial + ". -  ("
                        + sortedUsers[j].getTotalPointsEarned() +")";

            }
            else{
                leaderBoardUserInfo[j] = j+1 + ". "  + fullName  + " -  (" + sortedUsers[j].getTotalPointsEarned() +")";
            }
            Log.i("IN ORDER", leaderBoardUserInfo[j]);


        }
        populateListView(leaderBoardUserInfo);
    }

    private User[] sortUsersByTotalPoints(User[] allUsers) {

        User temp;
        for ( int i = 0; i< allUsers.length; i++){

            for (int j = i; j > 0; j--){

                if( allUsers[j].getTotalPointsEarned() > allUsers[j-1].getTotalPointsEarned()){
                    temp = allUsers[j];
                    allUsers[j] = allUsers[j-1];
                    allUsers[j-1] = temp;
                }
            }
        }
        return allUsers;
    }

    private void populateListView(String[] sortedUsersInfo) {
        //Build Array Adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                R.layout.leader_board_items,
                sortedUsersInfo);

        // Configure the list view
        ListView listView = findViewById(R.id.list_Leader_Board);
        listView.setAdapter(adapter);
    }
}
