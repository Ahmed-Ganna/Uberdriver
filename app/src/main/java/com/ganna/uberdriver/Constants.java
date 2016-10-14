package com.ganna.uberdriver;

/**
 * Created by Ahmed on 10/13/2016.
 */

public class Constants {
    public static final String NOTIFICATON_NODE = "driver_notifi";
    public static final String LOCATION_NODE = "location";
    public static final String DRIVER_LOCATION_NODE = "driver_location";
    public static final String RIDE_NODE = "ride";
    public static final String RIDE_STATUS_KEY = "status";
    public static final String STATUS_PICK_REQUESTED = "pick_requested";
    public static final String STATUS_PICK_ACCEPTED = "pick_accepted";
    public static final String PICKED_LATLNG = "pickedlatlng";
    public static final String DIRECTION_KEY ="AIzaSyCM26Zi8aWlmyAZJXZERECE5YdjpG4T-4I";
    public static final String BASE_DIRECTION_URL = "https://maps.googleapis.com/maps/api/directions/json?" +
            "origin=%1$s&destination=%2$s&key="+DIRECTION_KEY;
    public static final String DRIVER_LATLNG_KEY = "driver_latlng";
    public static final double INITIAL_LAT =30.0493881 ;
    public static final double INITIAL_LNG =31.2299758 ;
    public static final String STATUS_PICK_ARRIVED = "pick_driver_arrived";
    public static final String STATUS_PICK_STARTED = "pick_started";
    public static final String ADDRESS = "add";
    public static final String LAT = "lat";
    public static final String LNG = "lng";
    public static final String RIDE_RIDER_NAME = "pick_rider";

    public static final String CURRENT_LOC = "cur_loc";
}
