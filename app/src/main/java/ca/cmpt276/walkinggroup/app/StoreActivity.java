package ca.cmpt276.walkinggroup.app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ca.cmpt276.walkinggroup.dataobjects.Icon;
import ca.cmpt276.walkinggroup.dataobjects.Theme;
import ca.cmpt276.walkinggroup.dataobjects.Store;
import ca.cmpt276.walkinggroup.dataobjects.Title;
import ca.cmpt276.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroup.model.State;
import ca.cmpt276.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

/**
 * Able to purchase rewards. Able to see earned rewards
 */
public class StoreActivity extends AppCompatActivity {

    private static final String TAG = "StoreActivity";

    private Integer userPoints;

    private String stringCategory;
    private Store store;
    private State state = State.getInstance();
    private User user = state.getUser();
    WGServerProxy proxy = state.getProxy();

    ////////////// Titles Variables //////////
    private List<Title> allTitles = new ArrayList<>();
    private List<Title> availableTitles = new ArrayList<>();

    private Set<String> userTitlesSet = new HashSet<>();
    private List<Title> userTitles = new ArrayList<>();

    private String[] userTitlesArray;
    private String[] currentStoreTitlesArray;

    ////////////// Colour Schemes Variables //////////
    private List<Theme> allThemes = new ArrayList<>();
    private List<Theme> availableThemes = new ArrayList<>();

    private Set<String> userThemeSet = new HashSet<>();
    private List<Theme> userThemes = new ArrayList<>();

    private String[] userThemesArray;
    private String[] currentStoreThemesArray;


    /////////////// ICON VARIABLES //////////////////

    private List<Icon> allIcons = new ArrayList<>();
    private List<Icon> availableIcons = new ArrayList<>();

    private Set<String> userIconSet = new HashSet<>();
    private List<Icon> userIcons = new ArrayList<>();

    private String[] userIconsArray;
    private String[] currentStoreIconsArray;



    /////////// Intent Variables And Activity Specific  ///////////////////////////////
    private ListView storeList;
    private ListView purchasedList;

    public static final String EXTRA_CATEGORY = "ca.cmpt276.walkinggroup.app.StoreActivity-category";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Theme.setColourScheme(this);
        setContentView(R.layout.activity_store);

        storeList = findViewById(R.id.listAvailableInStore);
        purchasedList = findViewById(R.id.listPurchased);

        extractCategoryFromIntent();

        getServerUser();

        store = Store.getInstance();
        allTitles = store.getListofTitles();
        allThemes =store.getListOfThemes();
        allIcons = store.getListofIcons();

    }

    private void getServerUser() {
        Call<User> caller = proxy.getUserById(user.getId());
        ProxyBuilder.callProxy(StoreActivity.this, caller, returnedUser -> response(returnedUser));
    }

    private void response(User returnedUser){

        user = returnedUser;
        userPoints = user.getCurrentPoints();

        if(stringCategory.equals("Titles")){
            userTitles = user.getRewards().getEarnedTitles();
            userTitlesArray = new String[userTitles.size()];
            createTitleStringsArray(userTitles, userTitlesArray);

            populateTitleSet(userTitles, userTitlesSet);
            createTitleSubList(allTitles, userTitlesSet, availableTitles);
            currentStoreTitlesArray = new String[availableTitles.size()];
            createTitleStringsArray(availableTitles, currentStoreTitlesArray);

        }
        else if(stringCategory.equals("Themes")){
            userThemes = user.getRewards().getEarnedThemes();
            userThemesArray = new String[userThemes.size()];
            createThemeStringsArray(userThemes, userThemesArray);

            populateThemeSet(userThemes, userThemeSet);
            createThemeSubList(allThemes, userThemeSet, availableThemes);
            currentStoreThemesArray = new String[availableThemes.size()];
            createThemeStringsArray(availableThemes, currentStoreThemesArray);
        }

        else if(stringCategory.equals("Icons")){
            userIcons = user.getRewards().getEarnedIcons();
            userIconsArray = new String[userIcons.size()];
            createIconStringsArray(userIcons, userIconsArray);

            populateIconSet(userIcons, userIconSet);
            createIconSubList(allIcons, userIconSet, availableIcons);
            currentStoreIconsArray = new String[availableIcons.size()];
            createIconStringsArray(availableIcons, currentStoreIconsArray);
        }


        setupUI();
    }

 ///////////////////////////////////// GENERIC FUNCTION //////////////////////////////////////////////////////////////


    private void setupUI(){

        updatePoints();

//        TextView title = findViewById(R.id.txtStoreActivityTitle);
//        title.setText(getString(R.string.Rewards_title, stringCategory));

        TextView availableToBuy = findViewById(R.id.textAvailableTitle);
        availableToBuy.setText(getString(R.string.available_to_buy_txt, stringCategory));

        TextView purchasedRewards = findViewById(R.id.textPurchasedTitle);
        purchasedRewards.setText(getString(R.string.purchased_title, stringCategory ));

        if(stringCategory.equals("Titles")){
            populateListView(purchasedList, userTitlesArray);
            populateListView(storeList, currentStoreTitlesArray);
        }
        else if(stringCategory.equals("Themes")){
            populateListView(purchasedList, userThemesArray);
            populateListView(storeList, currentStoreThemesArray);
        }
        else if (stringCategory.equals("Icons")){
            populateListView(purchasedList, userIconsArray);
            populateListView(storeList, currentStoreIconsArray);
        }

        registerClick(purchasedList);
        registerClick(storeList);

    }


    private void populateListView(ListView listView, String[] strings){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                R.layout.groups_i_am_in_items,
                strings);

        // Configure the list view
        listView.setAdapter(adapter);
    }

    private void updatePoints(){
        TextView txtCurrentPoints = findViewById(R.id.txtCurrentPoints);
        txtCurrentPoints.setText(getString(R.string.current_points_txt, userPoints));
    }


//////////////////////////////////// TITLES SPECIFIC FUNCTIONS ///////////////////////////////////////////////////////


    private void createTitleStringsArray(List<Title> list, String[] array){
        for(int i=0; i<list.size(); i++){
            array[i]  = list.get(i).getTitleName() + " - (" + list.get(i).getPointsPrice() + " points)";
        }
    }

    private void populateTitleSet(List<Title> listTitles,  Set<String> setTitleNames){
        for(int i=0; i<listTitles.size(); i++){
            setTitleNames.add(listTitles.get(i).getTitleName());
        }

    }

    private void createTitleSubList(List<Title> all, Set<String> compare , List<Title> sub){
        for (int i =0; i<all.size(); i++){
            Title titleAll = new Title();
            titleAll = all.get(i);
            if(!compare.contains(titleAll.getTitleName())){
                sub.add(titleAll);
            }
        }

    }
//////////////////////////////////// THEMES SPECIFIC FUNCTIONS ///////////////////////////////////////////////////////

    private void createThemeStringsArray(List<Theme> themesList, String[] themesArray){
        for(int i=0; i<themesList.size(); i++){
            themesArray[i]  = themesList.get(i).getThemeName() + " - (" + themesList.get(i).getThemePoints() + " points)";
        }
    }

    private void populateThemeSet(List<Theme> themeList,  Set<String> setThemeNames){
        for(int i=0; i< themeList.size(); i++){
            setThemeNames.add(themeList.get(i).getThemeName());

        }
    }

    private void createThemeSubList(List<Theme> allThemes, Set<String> userThemes , List<Theme> remainingThemes){
        for (int i = 0; i < allThemes.size(); i++){
            Theme theme = new Theme();
            theme = allThemes.get(i);
            if(!userThemes.contains(theme.getThemeName())){
                remainingThemes.add(theme);
            }
        }

    }

    //////////////////////////////////// ICON SPECIFIC FUNCTIONS ///////////////////////////////////////////////////////

    private void createIconStringsArray(List<Icon> iconList, String[] iconArray){
        for(int i=0; i<iconList.size(); i++){
            iconArray[i]  = iconList.get(i).getIconName() + " - (" + iconList.get(i).getIconPoints() + " points)";
        }
    }

    private void populateIconSet(List<Icon> iconList,  Set<String> setIconNames){
        for(int i=0; i< iconList.size(); i++){
            setIconNames.add(iconList.get(i).getIconName());

        }
    }

    private void createIconSubList(List<Icon> allIcon, Set<String> userIcons , List<Icon> remainingIcons){
        for (int i = 0; i < allIcon.size(); i++){
            Icon icon = allIcon.get(i);
            if(!userIcons.contains(icon.getIconName())){
                remainingIcons.add(icon);
            }
        }

    }
//////////////////////////////////////// REGISTERING CLICKS ///////////////////////////////////////////////


    private void registerClick(ListView listView){
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                if (stringCategory.equals("Titles")){
                    if(listView == purchasedList){
                        user.getRewards().setCurrentTitle(userTitles.get(position));
                        updateUser();
                    }
                    if(listView == storeList){
                        if(userPoints >= availableTitles.get(position).getPointsPrice()){
                            purchaseAlertDialog(position);
                        }
                        else{
                            Toast.makeText(StoreActivity.this,
                                    getString(R.string.cannot_buy_message, availableTitles.get(position).getTitleName()),
                                    Toast.LENGTH_SHORT).
                                    show();
                        }
                    }

                }
                else if (stringCategory.equals("Themes")){
                    if(listView == purchasedList){
                        selectPurchasedTheme(position);
                    }
                    else if(listView == storeList){
                        if(userPoints >= availableThemes.get(position).getThemePoints()) {
                            purchaseAlertDialog(position);
                        }
                        else{
                            Toast.makeText(StoreActivity.this,
                                    getString(R.string.cannot_buy_theme_message, availableThemes.get(position).getThemeName()),
                                    Toast.LENGTH_SHORT).
                                    show();
                        }
                    }

                }
                else{
                    if(listView == purchasedList){
                        user.getRewards().setCurrentIcon(userIcons.get(position));
                        updateUser();
                    }
                    else if(listView == storeList){
                        if(userPoints >= availableIcons.get(position).getIconPoints()) {
                            purchaseAlertDialog(position);
                        }
                        else{
                            Toast.makeText(StoreActivity.this,
                                    getString(R.string.cannot_buy_icon_message, availableIcons.get(position).getIconName()),
                                    Toast.LENGTH_SHORT).
                                    show();
                        }
                    }

                }
                return true;

            }
        });
    }

    private void selectPurchasedTheme( int position) {
        user.getRewards().setCurrentTheme(userThemes.get(position));
        Theme.setCurrentTheme(userThemes.get(position).getThemeId());

        updateUser();
    }

    private void purchaseAlertDialog(int position){

        AlertDialog.Builder builder = new AlertDialog.Builder(StoreActivity.this);

        builder.setCancelable(true);
        if(stringCategory.equals("Themes")) {
            builder.setTitle(R.string.theme_alert_title);
            builder.setMessage(getString(R.string.alert_theme_message, availableThemes.get(position).getThemeName()));
            builder.setPositiveButton("BUY", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    buyTheme(position);
                }
            });
        }
        else if(stringCategory.equals("Titles")){
            builder.setTitle(R.string.alert_title_title);
            builder.setMessage(getString(R.string.alert_title_message, availableTitles.get(position).getTitleName()));
            builder.setPositiveButton("BUY", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    buyTitle(position);
                }
            });
        }
        else{
            builder.setTitle(R.string.alert_icon_title);
            builder.setMessage(getString(R.string.alert_icon_message, availableIcons.get(position).getIconName()));
            builder.setPositiveButton("BUY", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    buyIcon(position);
                }
            });
        }

        builder.setNegativeButton(R.string.cancelbutton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();

    }


    private void buyTitle(int position){
        userPoints -= availableTitles.get(position).getPointsPrice();
        user.setCurrentPoints(userPoints);

        user.getRewards().setCurrentTitle(availableTitles.get(position));

        userTitles.add(availableTitles.get(position));
        userTitlesArray = new String[userTitles.size()];
        createTitleStringsArray(userTitles, userTitlesArray);
        userTitlesSet = new HashSet<>();
        populateTitleSet(userTitles, userTitlesSet);
        availableTitles = new ArrayList<>();

        createTitleSubList(allTitles, userTitlesSet, availableTitles);
        currentStoreTitlesArray = new String[availableTitles.size()];

        createTitleStringsArray(availableTitles, currentStoreTitlesArray);
        populateListView(purchasedList, userTitlesArray);
        populateListView(storeList, currentStoreTitlesArray);
        updateUser();

    }

    private void buyTheme(int position){
        //updates User's current points
        userPoints -= availableThemes.get(position).getThemePoints();
        user.setCurrentPoints(userPoints);

        user.getRewards().setCurrentTheme(availableThemes.get(position));
        Theme.setCurrentTheme(availableThemes.get(position).getThemeId());

        userThemes.add(availableThemes.get(position));
        userThemeSet = new HashSet<>();
        populateThemeSet(userThemes, userThemeSet);
        availableThemes = new ArrayList<>();
        createThemeSubList(allThemes, userThemeSet, availableThemes);

        updateUser();
    }

    private void buyIcon(int position){
        userPoints -= availableIcons.get(position).getIconPoints();
        user.setCurrentPoints(userPoints);

        user.getRewards().setCurrentIcon(availableIcons.get(position));

        userIcons.add(availableIcons.get(position));
        userIconsArray = new String[userIcons.size()];
        createIconStringsArray(userIcons, userIconsArray);
        userIconSet = new HashSet<>();
        populateIconSet(userIcons, userIconSet);
        availableIcons = new ArrayList<>();

        createIconSubList(allIcons, userIconSet, availableIcons);
        currentStoreIconsArray = new String[availableIcons.size()];

        createIconStringsArray(availableIcons, currentStoreIconsArray);
        populateListView(purchasedList, userIconsArray);
        populateListView(storeList, currentStoreIconsArray);
        updateUser();

    }

    private void updateUser(){
        Call<User> caller = proxy.editUser(user.getId(), user);
        ProxyBuilder.callProxy(this, caller, returnedServerUser -> responsetoUpdate(returnedServerUser));

    }

    private void responsetoUpdate(User user){
        updatePoints();
        if(stringCategory.equals("Titles")) {
            Toast.makeText(this, getString(R.string.title_change_toast) + user.getRewards().getCurrentTitle().getTitleName(), Toast.LENGTH_SHORT).show();
        }
        else if(stringCategory.equals("Themes")){
            Toast.makeText(this, getString(R.string.theme_change_toast) + user.getRewards().getCurrentTheme().getThemeName(), Toast.LENGTH_SHORT).show();
            recreate();
        }
        else{
            Toast.makeText(this, getString(R.string.icon_change_toast) + user.getRewards().getCurrentIcon().getIconName(), Toast.LENGTH_SHORT).show();

        }
    }



//////////////////////// INTENT FUNCTIONS ////////////////////////////////////

    private void extractCategoryFromIntent() {
        Intent intent = getIntent();
        stringCategory =intent.getStringExtra(EXTRA_CATEGORY);
    }


    public static Intent makeStoreActivityIntent(Context context, String category) {
        Intent intent = new Intent(context, StoreActivity.class);
        intent.putExtra(EXTRA_CATEGORY, category);
        return intent;
    }
}
