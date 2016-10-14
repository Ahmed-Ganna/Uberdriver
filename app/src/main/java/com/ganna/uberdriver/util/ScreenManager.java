package com.ganna.uberdriver.util;

import android.app.Activity;
import android.content.Intent;

import com.ganna.uberdriver.Constants;
import com.ganna.uberdriver.ui.EnRootScreen;
import com.ganna.uberdriver.ui.PickUpScreen;

/**
 * Created by Ahmed on 10/14/2016.
 */

public class ScreenManager {
    public static  void launchPickUpScreen(Activity activity,Double lat,Double lng,String currentLocation){
        Intent intent = new Intent(activity,PickUpScreen.class);
        intent.putExtra(Constants.CURRENT_LOC,currentLocation);
        intent.putExtra(Constants.LAT,lat);
        intent.putExtra(Constants.LNG,lng);
        activity.startActivity(intent);
    }

    public static  void launchEnRootScreen(Activity activity,Double lat,Double lng,String riderName){
        Intent intent = new Intent(activity,EnRootScreen.class);
        intent.putExtra(Constants.LAT,lat);
        intent.putExtra(Constants.LNG,lng);
        intent.putExtra(Constants.RIDE_RIDER_NAME,riderName);
        activity.startActivity(intent);
    }
}
