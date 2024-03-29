package ca.cmpt276.walkinggroup.dataobjects;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * User class to store the data the server expects and returns.
 * (Incomplete: Needs support for monitoring and groups).
 */

// All model classes coming from server must have this next line.
// It ensures deserialization does not fail if server sends you some fields you are not expecting.
// This is needed for the server to be able to change without breaking your app!
@JsonIgnoreProperties(ignoreUnknown = true)
public class User extends IdItemBase {
    // NOTE: id, hasFullData, and href in IdItemBase base class.

    // Data fields for the user.
    // -------------------------------------------------------------------------------------------
    // NOTE: Make numbers Long/Integer, not long/int because only the former will
    //       deserialize if the value is null from the server.

    private String name;
    private String email;
    private Integer birthYear;
    private Integer birthMonth;
    private String address;
    private String cellPhone;
    private String homePhone;
    private String grade;
    private String teacherName;
    private String emergencyContactInfo;
    private String password;
    private static final String TOKEN_PREF_NAME = "Token Received";


    // Monitoring
    // - - - - - - - - - - - - - - - - - - - - - - - - - -
    private List<User> monitoredByUsers;
    private List<User> monitorsUsers;

    // Group Membership / Leading
    // - - - - - - - - - - - - - - - - - - - - - - - - - -
    private List<Group> memberOfGroups;
    private List<Group> leadsGroups;

    // GPS Location
    // - - - - - - - - - - - - - - - - - - - - - - - - - -
    private GpsLocation lastGpsLocation;

    // Messages
    // - - - - - - - - - - - - - - - - - - - - - - - - - -
    private List<Message> messages;

    // Gamification Support
    // - - - - - - - - - - - - - - - - - - - - - - - - - - -
    private Integer currentPoints;
    private Integer totalPointsEarned;
    // rewards will be serialized to be the customJson
    private EarnedRewards rewards;


    // Permissions
    // - - - - - - - - - - - - - - - - - - - - - - - - - - -
    private List<PermissionRequest> pendingPermissionRequests;





    //User hand back a reference to an object


    // Basic User Data
    // -------------------------------------------------------------------------------------------

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
            this.email = email;
    }


    // Note: Password never returned by the server; only used to send password to server.
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public Integer getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(Integer birthYear) {
        this.birthYear = birthYear;
    }


    public Integer getBirthMonth() {
        return birthMonth;
    }

    public void setBirthMonth(Integer birthMonth) {
        this.birthMonth = birthMonth;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }


    public String getCellPhone() {
        return cellPhone;
    }

    public void setCellPhone(String cellPhone) {
        this.cellPhone = cellPhone;
    }


    public String getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }


    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }


    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }


    public String getEmergencyContactInfo() {
        return emergencyContactInfo;
    }

    public void setEmergencyContactInfo(String emergencyContactInfo) {
        this.emergencyContactInfo = emergencyContactInfo;
    }


    // Monitoring
    // -------------------------------------------------------------------------------------------

    public List<User> getMonitoredByUsers() {

        return monitoredByUsers;
    }

    public void setMonitoredByUsers(List<User> monitoredByUsers) {

        this.monitoredByUsers = monitoredByUsers;
    }

    public List<User> getMonitorsUsers() {

        return monitorsUsers;
    }

    public void setMonitorsUsers(List<User> monitorsUsers) {

        this.monitorsUsers = monitorsUsers;

    }


    // Groups
    // -------------------------------------------------------------------------------------------
    public List<Group> getMemberOfGroups() {
        return memberOfGroups;
    }
    public void setMemberOfGroups(List<Group> memberOfGroups) {
        this.memberOfGroups = memberOfGroups;
    }

    public List<Group> getLeadsGroups() {
        return leadsGroups;
    }
    public void setLeadsGroups(List<Group> leadsGroups) {
        this.leadsGroups = leadsGroups;
    }


    // Rewards (custom JSON data)
    // -------------------------------------------------------------------------------------------
    public Integer getCurrentPoints() {
        return currentPoints;
    }
    public void setCurrentPoints(Integer currentPoints) {
        this.currentPoints = currentPoints;
    }

    public Integer getTotalPointsEarned() {
        return totalPointsEarned;
    }
    public void setTotalPointsEarned(Integer totalPointsEarned) {
        this.totalPointsEarned = totalPointsEarned;
    }

    //GPS Location
    // -------------------------------------------------------------------------------------------
    public GpsLocation getLastGpsLocation() {
        return lastGpsLocation;
    }

    public void setLastGpsLocation(GpsLocation lastGpsLocation) {
        this.lastGpsLocation = lastGpsLocation;
    }

    //Messages
    // -------------------------------------------------------------------------------------------
    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    //Permission Requests
    // -------------------------------------------------------------------------------------------
    public List<PermissionRequest> getPendingPermissionRequests() {
        return pendingPermissionRequests;
    }

    public void setPendingPermissionRequests(List<PermissionRequest> pendingPermissionRequests) {
        this.pendingPermissionRequests = pendingPermissionRequests;
    }

    // Setter will be called when deserializing User's JSON object; we'll automatically
    // expand it into the custom object.
    public void setCustomJson(String jsonString) {
        if (jsonString == null || jsonString.length() == 0) {
            rewards = null;
            Log.w("USER", "De-serializing string is null for User's custom Json rewards; ignoring.");
        } else {
            Log.w("USER", "De-serializing string: " + jsonString);
            try {
                rewards = new ObjectMapper().readValue(
                        jsonString,
                        EarnedRewards.class);
                Log.w("USER", "De-serialized embedded rewards object: " + rewards);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    // Having a getter will make this function be called to set the value of the
    // customJson field of the JSON data being sent to server.
    public String getCustomJson() {
        // Convert custom object to a JSON string:
        String customAsJson = null;
        try {
            customAsJson = new ObjectMapper().writeValueAsString(rewards);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return customAsJson;
    }

    public EarnedRewards getRewards() {
        return rewards;
    }
    public void setRewards(EarnedRewards rewards) {
        this.rewards = rewards;
    }

    // Utility Functions
    // -------------------------------------------------------------------------------------------
    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", birthYear=" + birthYear +
                ", birthMonth=" + birthMonth +
                ", address='" + address + '\'' +
                ", cellPhone='" + cellPhone + '\'' +
                ", homePhone='" + homePhone + '\'' +
                ", grade='" + grade + '\'' +
                ", teacherName='" + teacherName + '\'' +
                ", emergencyContactInfo='" + emergencyContactInfo + '\'' +
                ", password='" + password + '\'' +
                ", monitoredByUsers=" + monitoredByUsers +
                ", monitorsUsers=" + monitorsUsers +
                ", memberOfGroups=" + memberOfGroups +
                ", leadsGroups=" + leadsGroups +
                ", lastGpsLocation=" + lastGpsLocation +
                ", messages=" + messages +
                ", currentPoints=" + currentPoints +
                ", totalPointsEarned=" + totalPointsEarned +
                ", rewards=" + rewards +
                ", pendingPermissionRequests=" + pendingPermissionRequests +
                ", id=" + id +
                ", hasFullData=" + hasFullData +
                ", href='" + href + '\'' +
                '}';
    }
}
