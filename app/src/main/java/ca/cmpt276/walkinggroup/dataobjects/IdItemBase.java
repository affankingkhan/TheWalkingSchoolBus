package ca.cmpt276.walkinggroup.dataobjects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

/**
 * Base class for all items that have an ID and href from the server.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IdItemBase {
    // NOTE: Make numbers Long/Integer, not long/int because only the former will
    //       deserialize if the value is null from the server.
    protected Long id;
    protected Boolean hasFullData;
    protected String href;


    //Update: Dr.Brian took this away
//    public IdItemBase() {
//
//    }


    // Check if full data
    // -------------------------------------------------------------------------------------------
    // Server often replies with stub objects instead of full data.
    // If server sends back just an ID then it's a stub; otherwise you have full data about
    // *this* object. Objects it refers to, such as other users or groups, may not be filled in
    // (and hence those will have hasFullData set to false for them).
    public Boolean hasFullData() {
        return hasFullData;
    }
    public void setHasFullData(Boolean hasFullData) {
        this.hasFullData = hasFullData;
    }

    // Basic User Data
    // -------------------------------------------------------------------------------------------
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    // Link (unneeded, but send by server...)
    // -------------------------------------------------------------------------------------------
    public String getHref() {
        return href;
    }
    public void setHref(String href) {
        this.href = href;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IdItemBase idItem = (IdItemBase) o;
        return Objects.equals(getId(), idItem.getId());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getId());
    }


    @Override
    public String toString() {
        return "IdItemBase{" +
                "id=" + id +
                ", hasFullData=" + hasFullData +
                ", href='" + href + '\'' +
                '}';
    }
}
