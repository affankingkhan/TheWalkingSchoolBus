package ca.cmpt276.walkinggroup.dataobjects;

import java.util.ArrayList;
import java.util.List;


import ca.cmpt276.walkinggroup.app.R;

/**
 * Singleton pattern which stores all rewards
 */
public class Store {

    private List<Title> listofTitles;
    private List<Icon> listofIcons;
    private List<Theme> listOfThemes;

    private static Store instance;

    private Store(){
        listofTitles =  new ArrayList<>();
        listOfThemes = new ArrayList<>();
        listofIcons = new ArrayList<>();
        populateTitles();
        populateThemes();
        populateIcons();
    }



    public static Store getInstance(){
        if (instance == null){
            instance = new Store();
        }

        return instance;
    }

    private void populateThemes() {

        Theme colour0 = new Theme();
        colour0.setThemeName("Default");
        colour0.setThemeId(R.style.AppTheme);
        colour0.setThemePoints(0);
        listOfThemes.add(colour0);

        Theme colour1 = new Theme();
        colour1.setThemeName("Raging Pink");
        colour1.setThemeId(R.style.AppTheme_1);
        colour1.setThemePoints(800);
        listOfThemes.add(colour1);

        Theme colour2 = new Theme();
        colour2.setThemeName("Explosive Yellow");
        colour2.setThemeId(R.style.AppTheme_2);
        colour2.setThemePoints(1500);
        listOfThemes.add(colour2);

        Theme colour3 = new Theme();
        colour3.setThemeName("Vibing Purple");
        colour3.setThemeId(R.style.AppTheme_3);
        colour3.setThemePoints(2000);
        listOfThemes.add(colour3);

        Theme colour4 = new Theme();
        colour4.setThemeName("Electric Green");
        colour4.setThemeId(R.style.AppTheme_4);
        colour4.setThemePoints(2500);
        listOfThemes.add(colour4);

        Theme colour5 = new Theme();
        colour5.setThemeName("Icy Blue");
        colour5.setThemeId(R.style.AppTheme_5);
        colour5.setThemePoints(3000);
        listOfThemes.add(colour5);

    }
    private void populateTitles(){

        //////// Default Title ////////////////////////
        Title title5 = new Title();
        title5.setTitleName("Walker");
        title5.setPointsPrice(0);
        listofTitles.add(title5);

        Title title1 = new Title();
        title1.setTitleName("Wight Walker");
        title1.setPointsPrice(500);
        listofTitles.add(title1);

        Title title2 = new Title();
        title2.setTitleName("Ninja");
        title2.setPointsPrice(500);
        listofTitles.add(title2);

        Title title3 = new Title();
        title3.setTitleName("Usain Bolt");
        title3.setPointsPrice(1000);
        listofTitles.add(title3);

        Title title4 = new Title();
        title4.setTitleName("The Brofessor");
        title4.setPointsPrice(2000);
        listofTitles.add(title4);

    }

    private void populateIcons(){
        Icon icon1 = new Icon();
        icon1.setIconName("Default Android");
        icon1.setIconId(R.drawable.androidsss);
        icon1.setIconPoints(0);
        listofIcons.add(icon1);

        Icon icon2 = new Icon();
        icon2.setIconName("Michael Jackson");
        icon2.setIconId(R.drawable.jackson_moonwalk);
        icon2.setIconPoints(100);
        listofIcons.add(icon2);

        Icon icon3 = new Icon();
        icon3.setIconName("Naruto");
        icon3.setIconId(R.drawable.naruto);
        icon3.setIconPoints(100);
        listofIcons.add(icon3);

        Icon icon4 = new Icon();
        icon4.setIconName("Goku");
        icon4.setIconId(R.drawable.goku);
        icon4.setIconPoints(100);
        listofIcons.add(icon4);

        Icon icon5 = new Icon();
        icon5.setIconName("Cool");
        icon5.setIconId(R.drawable.cool);
        icon5.setIconPoints(300);
        listofIcons.add(icon5);

    }


///////////////////////////////// GETTERS ////////////////////////////////////////


    public List<Title> getListofTitles() {
        return listofTitles;
    }

    public List<Icon> getListofIcons() {
        return listofIcons;
    }

    public List<Theme> getListOfThemes(){
        return listOfThemes;
    }
}
