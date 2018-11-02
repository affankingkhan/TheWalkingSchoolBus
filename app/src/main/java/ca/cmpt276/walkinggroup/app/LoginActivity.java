package ca.cmpt276.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import ca.cmpt276.walkinggroup.model.State;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.dataobjects.Theme;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

/**
 * Saves token when logged in. Doesn't go back to this activity once logged in
 */

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private User newUser;
    private State state = State.getInstance();
    WGServerProxy proxy= state.getProxy();

    SharedPreferences pref;
    SharedPreferences.Editor editor ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pref = getSharedPreferences("token",MODE_PRIVATE);
        editor= pref.edit();


        newUser = new User();
        setUpOkLoginButton();
        setUpCreateAccountButton();


    }

    public static Intent makeLoginIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    private void setUpCreateAccountButton() {
        Button btnCreateAccount = findViewById(R.id.btnGoToCreateUser);
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent createNewUserIntent = CreateAccountActivity.makeCreateAccountActivityIntent(LoginActivity.this);
                startActivity(createNewUserIntent);
            }
        });
    }

    private void setUpOkLoginButton() {

        Button btnLogin = findViewById(R.id.btnConfirm);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText editEmail = findViewById(R.id.editEmailLogin);
                EditText editPassword = findViewById(R.id.editPasswordLogin);

                //Extract data from UI
                String userEmail = editEmail.getText().toString();
                String userPassword = editPassword.getText().toString();

                newUser.setEmail(userEmail);
                newUser.setPassword(userPassword);

                loggingIn( newUser);


            }



            ///////////// These functions logs you in. Please do not change. ////////////////////////////////////////////////////

            private void loggingIn( User user){

                ProxyBuilder.setOnTokenReceiveCallback(token -> state.storeToken(token));
                Call<Void> caller = proxy.login(user);
                ProxyBuilder.callProxy(LoginActivity.this, caller, nothing -> response(nothing));

            }
            // Responding to Log in ///
            private void response(Void nothing) {

                proxy = state.getProxy();

                Call<User> caller = proxy.getUserByEmail(newUser.getEmail());
                ProxyBuilder.callProxy(LoginActivity.this,caller, returnedUser-> respondToGetUser(returnedUser));




            }

            private void respondToGetUser(User returnedUser) {

                //Supports current theme chosesn by user to run as when opened
                int currentTheme = returnedUser.getRewards().getCurrentTheme().getThemeId();
                Theme.setCurrentTheme(currentTheme);

                state.putID(returnedUser.getId());

                editor.putString("token",state.getToken());
                editor.putLong("id", state.getUser().getId());
                editor.apply();

                Intent intentMenuActivity = MenuActivity.makeMenuIntent(LoginActivity.this);
                startActivity(intentMenuActivity);

                Intent resultIntent = new Intent();
                resultIntent.putExtra("Log In successfull",1);
                setResult(RESULT_OK,resultIntent);

                finish();

            }

            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        });
    }


}
