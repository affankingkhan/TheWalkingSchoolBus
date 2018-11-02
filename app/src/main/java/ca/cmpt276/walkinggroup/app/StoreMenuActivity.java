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

import ca.cmpt276.walkinggroup.dataobjects.Theme;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.model.State;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

/**
 * Menu to see different rewards
 */
public class StoreMenuActivity extends AppCompatActivity {

    private State state = State.getInstance();
    private WGServerProxy proxy = state.getProxy();
    private User user = state.getUser();

    private String[] arrayCategories = new String[3];
    private ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Theme.setColourScheme(this);
        setContentView(R.layout.activity_store_menu);

        listView = findViewById(R.id.listView_Categories);

        generateAllCategories();
        populateListView(arrayCategories);
    }
    private void generateAllCategories(){
        arrayCategories[0] = "Titles";
        arrayCategories[1] = "Icons";
        arrayCategories[2] = "Themes";
        // can always add more
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Theme.changeTheme(this, Theme.getCurrentTheme());
    }

    private void populateListView(String [] allCategories) {

        //Build Array Adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                R.layout.all_groups_items,
                allCategories);

        // Configure the list view
        listView.setAdapter(adapter);
        setupCategoryClick();
        getUser();
    }


    private void setupCategoryClick() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String categoryString = arrayCategories[position];
                Intent intent = StoreActivity.makeStoreActivityIntent(StoreMenuActivity.this,categoryString);
                startActivity(intent);

            }
        });

    }

    private void getUser() {
        Call<User> caller = proxy.getUserById(user.getId());
        ProxyBuilder.callProxy(StoreMenuActivity.this, caller, response -> responseCurrentPoints(response));
    }

    private void responseCurrentPoints(User responseUser) {
        TextView txtCurrentPoints = findViewById(R.id.txtCurrentPointsMenu);
        txtCurrentPoints.setText(getString(R.string.store_menu_current_points, responseUser.getCurrentPoints()) );
    }

//////////////////////// INTENT ////////////////////////////////////

    public static Intent makeStoreMenuIntent(Context context) {
        return new Intent(context, StoreMenuActivity.class);
    }

}
