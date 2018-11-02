package ca.cmpt276.walkinggroup.app;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import ca.cmpt276.walkinggroup.model.State;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;

/**
 * Displays buttons for Login, Create Account and Server Test Activity
 */

public class MainActivity extends AppCompatActivity {

    SharedPreferences pref ;
    SharedPreferences.Editor editor;

    State state = State.getInstance();
    WGServerProxy proxy = state.getProxy();

    private ImageView schoolBusImage;
    long animationDuration = 2500;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       schoolBusImage = findViewById(R.id.imageSchoolBus);

        AutoLogIn();
        setUpCreateNewAccountButton();
        setUpLoginButton();
        //setUpGoToTestServer();

        setupMoveBus();
    }


    private void AutoLogIn(){

        //////////////////////////////  Auto Log In Code  /////////////////////////////////////////////////////////////

        pref= getSharedPreferences("token",MODE_PRIVATE);
        editor  = pref.edit();



        if(pref.getString("token","")!= "" && pref.getLong("id",0)!= 0){

            state.storeToken(pref.getString("token",""));
            state.putID(pref.getLong("id",0));
            Intent intent = MenuActivity.makeMenuIntent(MainActivity.this);
            startActivity(intent);
            finish();


        }

        ////////////////////////////////////////////////////////////////////////////////////////////////////////

    }

    private void setUpCreateNewAccountButton() {
        Button btnCreateNewAccount = findViewById(R.id.btnCreateNewAccount);
        btnCreateNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Clicked 'Create New Account", Toast.LENGTH_SHORT)
                        .show();
                Intent createAccountIntent = CreateAccountActivity.makeCreateAccountActivityIntent(MainActivity.this);
                startActivity(createAccountIntent);

            }
        });

    }

    private void setUpLoginButton() {
        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = LoginActivity.makeLoginIntent(MainActivity.this);
                startActivityForResult(loginIntent,100);
                // Checks for result to see if this activity should close;


            }
        });
    }


    ///////////////////// Receives result from log in activity, if successful, this activity finishes. /////////
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode ==100) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

                finish();
            }
        }
    }





//    public void setUpGoToTestServer(){
//        Button btnNext = findViewById(R.id.btnNext);
//        btnNext.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = ServerTestActivity.makeLaunchIntent(MainActivity.this);
//                startActivity(intent);
//            }
//        });
//
//        }

    public static Intent makeMainIntent(Context context) {
        return new Intent(context, MainActivity.class);
    }

    private void setupMoveBus() {
        ObjectAnimator animatorX =ObjectAnimator.ofFloat(schoolBusImage,"x", 300f);
        animatorX.setDuration(animationDuration);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animatorX);
        animatorSet.start();

    }
}
