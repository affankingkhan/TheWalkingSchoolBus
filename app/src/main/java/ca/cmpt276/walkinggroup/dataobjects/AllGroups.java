package ca.cmpt276.walkinggroup.dataobjects;

/**
 * Class to store all groups from the server. SINGLETON - used in other activities like start walk
 */

import java.util.ArrayList;
import java.util.List;

public class AllGroups {

    private List<Group> listogroups;
    private Group group;


    private static AllGroups instance;

    private AllGroups(){
        listogroups =  new ArrayList<>();
    }

    public static AllGroups getInstance(){
        if (instance == null){
            instance = new AllGroups();
        }

        return instance;
    }



    public List<Group> getListogroups() {
        return listogroups;
    }

    public void setListogroups(List<Group> listogroups) {
        this.listogroups = listogroups;
    }
}
