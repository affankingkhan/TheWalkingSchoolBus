package ca.cmpt276.walkinggroup.dataobjects;
/**
 * This class is for creating an object called Icon which stores and image, the name of the image and the price
 */

import android.widget.ImageView;

import ca.cmpt276.walkinggroup.app.R;

public class Icon {
    private String iconName;
    private int iconId;
    private Integer iconPoints;


    private static int currentIcon;

    private final static int ICON_DEFAULT = R.drawable.android;




/////////////////////////// GETTERS AND SETTERS ////////////////////////////


    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public int getIconId() {
        return iconId;
    }

    public void setIconId(int iconId) {
        this.iconId = iconId;
    }

    public Integer getIconPoints() {
        return iconPoints;
    }

    public void setIconPoints(Integer iconPoints) {
        this.iconPoints = iconPoints;
    }

    public static int getCurrentIcon() {
        return currentIcon;
    }

    public static void setCurrentIcon(int currentIcon) {
        Icon.currentIcon = currentIcon;
    }
}
