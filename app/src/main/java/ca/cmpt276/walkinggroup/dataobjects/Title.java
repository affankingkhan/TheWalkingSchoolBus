package ca.cmpt276.walkinggroup.dataobjects;

/**
 * TItle class: This class is for creating rewards of type Title with a title name and price
 */

public class Title {
    private String titleName;
    private Integer pointsPrice;



///////////////////////////// GETTERS AND SETTERS ///////////////////////////////////////////


    public String getTitleName() {
        return titleName;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    public Integer getPointsPrice() {
        return pointsPrice;
    }

    public void setPointsPrice(Integer pointsPrice) {
        this.pointsPrice = pointsPrice;
    }
}