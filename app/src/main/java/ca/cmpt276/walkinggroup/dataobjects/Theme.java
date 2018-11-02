package ca.cmpt276.walkinggroup.dataobjects;

import android.app.Activity;
import android.content.Intent;

import ca.cmpt276.walkinggroup.app.R;

/**
 * Theme class: This class is for creating rewards and setting rewards of type Theme with a theme name,
 * colour number ( R.style.theme) and theme's price
 */
public class Theme {

    private String themeName;
    private int themeId;
    private Integer themePoints;


    private static int currentTheme;

    private final static int THEME_DEFAULT = R.style.AppTheme;
    private final static int THEME_PINK = R.style.AppTheme_1;
    private final static int THEME_YELLOW = R.style.AppTheme_2;
    private final static int THEME_PURPLE = R.style.AppTheme_3;
    private final static int THEME_GREEN = R.style.AppTheme_4;
    private final static int THEME_BLUE = R.style.AppTheme_5;


    public Theme(){

    }

    ///////////////////////////// GETTERS AND SETTERS ///////////////////////////////////////////

    public String getThemeName() {
        return themeName;
    }

    public void setThemeName(String themeName) {
        this.themeName = themeName;
    }

    public int getThemeId() {
        return themeId;
    }

    public void setThemeId(int themeId) {
        this.themeId = themeId;
    }

    public Integer getThemePoints() {
        return themePoints;
    }

    public void setThemePoints(Integer themePoints) {
        this.themePoints = themePoints;
    }


    public static int getCurrentTheme() {
        return currentTheme;
    }

    public static void setCurrentTheme(int theme) {
        currentTheme = theme;
    }


    /////////////////////////////////////////////////////////////////////////////////////////////


    public static void changeTheme(Activity activity, int theme)
    {
        currentTheme = theme;
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
    }


    public static void setColourScheme(Activity activity){
        switch (currentTheme){
            default:
            case THEME_DEFAULT:
                activity.setTheme(R.style.AppTheme);
                break;
            case THEME_PINK:
                activity.setTheme(R.style.AppTheme_1);
                break;
            case THEME_YELLOW:
                activity.setTheme(R.style.AppTheme_2);
                break;
            case THEME_PURPLE:
                activity.setTheme(R.style.AppTheme_3);
                break;
            case THEME_GREEN:
                activity.setTheme(R.style.AppTheme_4);
                break;
            case THEME_BLUE:
                activity.setTheme(R.style.AppTheme_5);
                break;

        }
    }

}
