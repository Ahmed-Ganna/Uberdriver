package com.ganna.uberdriver.managers;

import com.ganna.uberdriver.Constants;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Ahmed on 01/08/2016.
 */

public class FireManager {

    public static DatabaseReference getNotificationNode(){
        return FirebaseDatabase.getInstance().getReference().child(Constants.NOTIFICATON_NODE);
    }

    public static DatabaseReference getLocationNode(){
        return FirebaseDatabase.getInstance().getReference().child(Constants.LOCATION_NODE);
    }

    public static DatabaseReference getRideNode() {
        return FirebaseDatabase.getInstance().getReference().child(Constants.RIDE_NODE);
    }

}
