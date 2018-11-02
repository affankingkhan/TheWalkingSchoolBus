package ca.cmpt276.walkinggroup.dataobjects;

import android.location.Address;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


/**
 * Store information about a GPS location of a user.
 *
 * WARNING: INCOMPLETE! Server returns more information than this.
 * This is just to be a placeholder and inspire you how to do it.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
//public class GpsLocation {
//
//    private Address SourceAddress;
//    private Address DestinationAddress;
//
//    private double[] latarray;
//    private double[] longarray;
//
//
//
//
//    private static GpsLocation instance;
//
//    private GpsLocation(){
//        latarray = new double[2];
//        longarray = new double[2];
//
//        //private to prevent anyone else from instantiating
//    }
//    //User hand back a reference to an object
//    public static GpsLocation getInstance(){
//        if (instance == null){
//            instance = new GpsLocation();
//        }
//
//        return instance;
//    }
//
//    public Address getSourceAddress() {
//        return SourceAddress;
//    }
//
//    public void setSourceAddress(Address sourceAddress) {
//        SourceAddress = sourceAddress;
//    }
//
//    public Address getDestinationAddress() {
//        return DestinationAddress;
//    }
//
//    public void setDestinationAddress(Address destinationAddress) {
//        DestinationAddress = destinationAddress;
//    }
//
//    public double[] getLatarray() {
//        return latarray;
//    }
//
//    public void setLatarray(double[] latarray) {
//        this.latarray = latarray;
//    }
//
//    public double[] getLongarray() {
//        return longarray;
//    }
//
//    public void setLongarray(double[] longarray) {
//        this.longarray = longarray;
//    }
//}

public class GpsLocation {
    private Double lat;
    private Double lng;
    private String timestamp;



    public Double getLat() {
        return lat;
    }
    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }
    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "GpsLocation{" +
                "lat=" + lat +
                ", lng=" + lng +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
