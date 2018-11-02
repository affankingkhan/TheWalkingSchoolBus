package ca.cmpt276.walkinggroup.dataobjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom class that your group can change the format of in (almost) any way you like
 * to encode the rewards that this user has earned.
 *
 * This class gets serialized/deserialized as part of a User object. Server stores it as
 * a JSON string, so it has no direct knowledge of what it contains.
 * (Rewards may not be used during first project iteration or two)
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EarnedRewards {

    private Title currentTitle;
    private List<Title> earnedTitles;

    private Theme currentTheme;
    private List<Theme> earnedThemes;

    private Icon currentIcon;
    private List<Icon> earnedIcons;



//    private String title = "Dragon slayer";
//    private List<File> possibleBackgroundFiles = new ArrayList<>();
//    private Integer selectedBackground = 1;
//    private Integer titleColor = Color.BLUE;

    // Needed for JSON deserialization
    public EarnedRewards() {

    }

    public List<Title> getEarnedTitles() {
        return earnedTitles;
    }

    public void setEarnedTitles(List<Title> earnedTitles) {
        this.earnedTitles = earnedTitles;
    }

    public Title getCurrentTitle() {
        return currentTitle;
    }

    public void setCurrentTitle(Title currentTitle) {
        this.currentTitle = currentTitle;
    }

    public List<Theme> getEarnedThemes(){
        return earnedThemes;
    }

    public void setEarnedThemes(List<Theme> earnedThemes) {
        this.earnedThemes = earnedThemes;
    }

    public Theme getCurrentTheme() {
        return currentTheme;
    }

    public void setCurrentTheme(Theme currentTheme) {
        this.currentTheme = currentTheme;
    }

    public Icon getCurrentIcon() {
        return currentIcon;
    }

    public void setCurrentIcon(Icon currentIcon) {
        this.currentIcon = currentIcon;
    }

    public List<Icon> getEarnedIcons() {
        return earnedIcons;
    }

    public void setEarnedIcons(List<Icon> earnedIcons) {
        this.earnedIcons = earnedIcons;
    }

    //    public String getTitle() {
//        return title;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }
//
//    public List<File> getPossibleBackgroundFiles() {
//        return possibleBackgroundFiles;
//    }
//
//    public void setPossibleBackgroundFiles(List<File> possibleBackgroundFiles) {
//        this.possibleBackgroundFiles = possibleBackgroundFiles;
//    }
//
//    public int getSelectedBackground() {
//        return selectedBackground;
//    }
//
//    public void setSelectedBackground(int selectedBackground) {
//        this.selectedBackground = selectedBackground;
//    }
//
//    public int getTitleColor() {
//        return titleColor;
//    }
//
//    public void setTitleColor(int titleColor) {
//        this.titleColor = titleColor;
//    }


    @Override
    public String toString() {
        return "EarnedRewards{" +
                ", currentTitle=" + currentTitle +
                ", earnedTitles='" + earnedTitles +
                ", currentTheme=" + currentTheme +
                ", earnedThemes='" + earnedThemes +
                ", currentIcon=" + currentIcon +
                ", earnedIcons='" + earnedIcons +
                '}';
    }


//    @Override
//    public String toString() {
//        return "EarnedRewards{" +
//                "title='" + title + '\'' +
//                ", possibleBackgroundFiles=" + possibleBackgroundFiles +
//                ", selectedBackground=" + selectedBackground +
//                ", titleColor=" + titleColor +
//                '}';
//    }
}