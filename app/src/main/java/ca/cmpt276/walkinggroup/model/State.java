package ca.cmpt276.walkinggroup.model;

import android.content.Context;

import android.util.Log;
import android.widget.Toast;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


import ca.cmpt276.walkinggroup.dataobjects.Theme;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;

/**
 *This is the model singleton class. It references the proxy, updates proxy, etc.
 *Whenever you want to contact server, you have to get proxy from this class.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class State {


    public final String API_KEY = "9BDCC86A-70A2-4C26-A6D2-ABB7AA439517";


    private WGServerProxy proxy;
    private String TAG= "test";


    private String token;

    private User user;

    private Context context;



    private static State instance;

    private State(){
        //private to prevent anyone else from instantiating

        proxy = ProxyBuilder.getProxy(API_KEY);
        user=new User();
        token = "";



    }
    //User hand back a reference to an object
    public static State getInstance(){
        if (instance == null){
            instance = new State();
        }

        return instance;
    }


    public WGServerProxy getProxy() {

         return this.proxy;
    }

    //////////////////// Create User functions /////////////////////////


    ///////////////////////////////////////////////////////



    ///////////// log in functions ///////////////////



    private void onReceiveToken(String token) {

        Log.w(TAG, "   --> NOW HAVE TOKEN: " + token);
        this.token=token;

        this.proxy = ProxyBuilder.getProxy(API_KEY, token);

    }



    /////////////////////////////////////////////////////////////////////


    ///////////// Log out /////////////

    public void Logout(){

        proxy = ProxyBuilder.getProxy(API_KEY);
        this.token="";

    }
    //////////////////////////////////



    private void notifyUserViaLogAndToast(Context context, String message) {
        Log.w(TAG, message);
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }


    public User getUser() {
        return user;
    }


    /////// Please do not use functions in here. /////////

    public void putID(Long id){

        this.user.setId(id);

    }

    public void storeToken(String token){

        onReceiveToken(token);

    }

    public String getToken(){

        return this.token;
    }

//    public void putTheme(Theme theme){
//        this.user.getRewards().setCurrentTheme(theme);
//    }

    //////////////////////////////////////////////////

}
