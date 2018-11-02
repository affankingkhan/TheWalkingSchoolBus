package ca.cmpt276.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.service.quicksettings.Tile;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ca.cmpt276.walkinggroup.dataobjects.EarnedRewards;
import ca.cmpt276.walkinggroup.dataobjects.Icon;
import ca.cmpt276.walkinggroup.dataobjects.Store;
import ca.cmpt276.walkinggroup.dataobjects.Theme;
import ca.cmpt276.walkinggroup.dataobjects.Title;
import ca.cmpt276.walkinggroup.model.State;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

/**
 * Creates an account with name, email and password
 */
public class CreateAccountActivity extends AppCompatActivity {

    private static final String TAG = "CreateAccount";
    private State state = State.getInstance();
    WGServerProxy proxy = state.getProxy();

    private Store store = Store.getInstance();

    private List<Title> titles = new ArrayList<>();

    private List<Theme> themes = new ArrayList<>();

    private List<Icon> icons = new ArrayList<>();

    SharedPreferences pref ;
    SharedPreferences.Editor editor ;

    private User newUser = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        pref = getSharedPreferences("token",MODE_PRIVATE);
        editor= pref.edit();



        setUpCreateAccountButton();
    }


    //Create new user activity can make itself
    public static Intent makeCreateAccountActivityIntent(Context context) {
        return new Intent(context, CreateAccountActivity.class);
    }

    //Get user by email and id first
    private void setUpCreateAccountButton() {
        Button btnConfirmCreateNewAccount = findViewById(R.id.btnRegister);
        btnConfirmCreateNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText editName = findViewById(R.id.editName);
                EditText editEmail = findViewById(R.id.editEmail);
                EditText editPassword = findViewById(R.id.editPassword);

                //Extract data from UI
                String userName = editName.getText().toString();
                String userEmail = editEmail.getText().toString();
                String userPassword = editPassword.getText().toString();

                newUser.setName(userName);
                newUser.setEmail(userEmail);
                newUser.setPassword(userPassword);
                newUser.setCurrentPoints(1000);
                newUser.setTotalPointsEarned(1000);
                EarnedRewards earnedRewards= new EarnedRewards();

                Icon icon = store.getListofIcons().get(0);
                icons.add(icon);
                earnedRewards.setEarnedIcons(icons);
                earnedRewards.setCurrentIcon(icon);


                Title title = store.getListofTitles().get(0);
                titles.add(title);
                earnedRewards.setEarnedTitles(titles);
                earnedRewards.setCurrentTitle(title);

                Theme defaultTheme = store.getListOfThemes().get(0);
                themes.add(defaultTheme);
                earnedRewards.setEarnedThemes(themes);
                earnedRewards.setCurrentTheme(defaultTheme);

                Theme.setCurrentTheme(defaultTheme.getThemeId());

                newUser.setRewards(earnedRewards);


                //This creates a new user on server //
                Call<User> caller= proxy.createUser(newUser);
                ProxyBuilder.callProxy(CreateAccountActivity.this,caller, returnedUser -> response(returnedUser));



            }

            ////////////////////////// These Functions creates user, logs in, and set state.user's id //////////////////////////

            private void response(User returnedUser) {
                Toast.makeText(CreateAccountActivity.this, R.string.free_points_sign_up, Toast.LENGTH_LONG).show();
                loggingIn( newUser);
            }

            private void loggingIn( User user){

                ProxyBuilder.setOnTokenReceiveCallback(token -> state.storeToken(token));
                Call<Void> caller = proxy.login(user);
                ProxyBuilder.callProxy(CreateAccountActivity.this, caller, nothing -> respond(nothing));

            }
            // Responding to Log in ///
            private void respond(Void nothing) {

                proxy = state.getProxy();

                Call<User> caller = proxy.getUserByEmail(newUser.getEmail());
                ProxyBuilder.callProxy(CreateAccountActivity.this,caller, returnedUser-> respondToGetUser(returnedUser));




            }

            private void respondToGetUser(User returnedUser) {

                state.putID(returnedUser.getId());

                editor.putString("token",state.getToken());
                editor.putLong("id", state.getUser().getId());
//                editor.putString("currentUser theme", returnedUser.getRewards().getCurrentTheme());

                editor.apply();

                Intent intentMenuActivity = MenuActivity.makeMenuIntent(CreateAccountActivity.this);
                startActivity(intentMenuActivity);
                finish();

            }


            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////




        });
    }

}

