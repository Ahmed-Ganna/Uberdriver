package com.ganna.uberdriver.util;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by Ahmed on 10/14/2016.
 */

public class MapUtil {
    public static String getAddress(Context context, double lat, double lng) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);
            String add = obj.getAddressLine(0);
            add = add + "," + obj.getCountryName();
            add = add + "," + obj.getCountryCode();
            add = add + "," + obj.getAdminArea();
            add = add + "," + obj.getPostalCode();
            add = add + "," + obj.getSubAdminArea();
            add = add + "," + obj.getLocality();
            add = add + "," + obj.getSubThoroughfare();
            add= add.replace("null,","");
            add= add.replace(",null","");
            add= add.replace("Unnamed,","");
            add= add.replace(",Unnamed","");
            Log.v("IGA", "Address" + add);
            return add;
        } catch (IOException e) {
            e.printStackTrace();
            return "Address : unknown";
        }
    }
}
