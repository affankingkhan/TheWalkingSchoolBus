package ca.cmpt276.walkinggroup.dataobjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;



/**
 * Store information about the walking groups.
 *
 * WARNING: INCOMPLETE! Server returns more information than this.
 * This is just to be a placeholder and inspire you how to do it.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Group extends IdItemBase{

    private String groupDescription;

    // Route points (meeting to target)
    private double[] routeLatArray = new double[0];
    private double[] routeLngArray = new double[0];

    private User leader;
    private Set<User> memberUsers = new HashSet<>();
    private String customJson;



    public String getGroupDescription() {
        return groupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }

    public double[] getRouteLatArray() {
        return routeLatArray;
    }

    public void setRouteLatArray(double[] routeLatArray) {
        this.routeLatArray = routeLatArray;
    }

    public double[] getRouteLngArray() {
        return routeLngArray;
    }

    public void setRouteLngArray(double[] routeLngArray) {
        this.routeLngArray = routeLngArray;
    }

    public User getLeader() {
        return leader;
    }

    public void setLeader(User leader) {
        this.leader = leader;
    }

    public Set<User> getMemberUsers() {
        return memberUsers;
    }

    public void setMemberUsers(Set<User> memberUsers) {
        this.memberUsers = memberUsers;
    }

    public String getCustomJson() {
        return customJson;
    }

    public void setCustomJson(String customJson) {
        this.customJson = customJson;
    }

    @Override
    public String toString() {
        return "Group{" +
                "groupDescription='" + groupDescription + '\'' +
                ", routeLatArray=" + Arrays.toString(routeLatArray) +
                ", routeLngArray=" + Arrays.toString(routeLngArray) +
                ", leader=" + leader +
                ", memberUsers=" + memberUsers +
                ", customJson='" + customJson + '\'' +
                ", id=" + id +
                ", hasFullData=" + hasFullData +
                ", href='" + href + '\'' +
                '}';
    }











//    //private Long id;
//    private String groupDescription;
//    private double[] routeLatArray;
//    private double[] routeLngArray;
//    private User leader;
//    private List<User> memberUsers;
//    private String href;
//
//
//    /////Constructors///////
//
//    public Group(){
//
//        memberUsers = new ArrayList<>();
//        //routeLatArray = new double[2];
//        //routeLngArray = new double[2];
//
//    }
//
//    //////// Getters //////////////
//
//
//
//
//    public double[] getRouteLatArray() {
//        return routeLatArray;
//    }
//
//    public double[] getRouteLngArray() {
//        return routeLngArray;
//    }
//
//    public List<User> getMemberUsers() {
//        return memberUsers;
//    }
//
//    public String getGroupDescription() {
//        return groupDescription;
//    }
//
//
//
//    public User getLeader() {
//        return leader;
//    }
//
//    ////////////// Setters ////////////////
//
//
//    public void setGroupDescription(String groupDescription) {
//        this.groupDescription = groupDescription;
//    }
//
//
//
//    public void setLeader(User leader) {
//        this.leader = leader;
//    }
//
//
//    public void setRouteLatArray(double[] routeLatArray) {
//        this.routeLatArray = routeLatArray;
//    }
//
//    public void setRouteLngArray(double[] routeLngArray) {
//        this.routeLngArray = routeLngArray;
//    }
//
//    public void addUserToGroup(User user){
//
//        this.memberUsers.add(user);
//
//    }
//
//    public void setMemberUsersList(List<User> memberUsers){
//
//        this.memberUsers = memberUsers;
//
//    }

}
