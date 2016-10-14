package com.ganna.uberdriver.ui;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.ganna.uberdriver.Constants;
import com.ganna.uberdriver.R;
import com.ganna.uberdriver.managers.FireManager;
import com.ganna.uberdriver.util.ScreenManager;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import butterknife.ButterKnife;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainScreen extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LAC_REA_PERMISSION = 12;
    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private static NotificationManager mNotificationManager;
    private Criteria crita;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        crita = new Criteria();
        crita.setAccuracy(Criteria.ACCURACY_FINE);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getRideStatus();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{ACCESS_FINE_LOCATION}, LAC_REA_PERMISSION);
        }
    }


    private void getRideStatus() {
        FireManager.getRideNode()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String status = (String) dataSnapshot.child(Constants.RIDE_STATUS_KEY).getValue();
                        Double lat = (Double) dataSnapshot.child(Constants.PICKED_LATLNG).child("lat").getValue();
                        Double lng = (Double) dataSnapshot.child(Constants.PICKED_LATLNG).child("lng").getValue();
                        String riderName = (String) dataSnapshot.child(Constants.RIDE_RIDER_NAME).getValue();
                        switch (status) {
                            case Constants.STATUS_PICK_REQUESTED:
                                startNotification(riderName);
                                return;
                            case Constants.STATUS_PICK_ACCEPTED:
                                if (PickUpScreen.isActive){
                                    return;
                                }
                                ScreenManager.launchPickUpScreen(MainScreen.this,lat,lng,getCurrentLocation());
                                return;
                            case Constants.STATUS_PICK_ARRIVED:
                                if (EnRootScreen.isActive){
                                    return;
                                }
                                ScreenManager.launchEnRootScreen(MainScreen.this,lat,lng,riderName);
                                return;
                            case Constants.STATUS_PICK_STARTED:
                                sendLocationUpdates();
                                return;
                            default: //SESSION CLEARED
                                mMap.clear();

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }


    private void startNotification(String riderName) {
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        long[] pattern = {500,500,500,500,500,500,500,500,500};
        Intent switchIntent = new Intent(this, switchButtonListener.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0,
                switchIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Uber")
                .setContentText(riderName+" need a pickup , Accept !")
                .setVibrate(pattern)
                .setContentIntent(pi)
                .setPriority(Notification.PRIORITY_MAX)
                .setSound(alarmSound)
                .setAutoCancel(true);


        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }


    public static class switchButtonListener extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mNotificationManager.cancel(0);
            acceptPickUp();

        }

        private void acceptPickUp() {
            FireManager.getRideNode().child(Constants.RIDE_STATUS_KEY).setValue(Constants.STATUS_PICK_ACCEPTED);
        }
    }


    private String getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return Constants.INITIAL_LAT+","+Constants.INITIAL_LAT;
        }
        Location oldLocation = mLocationManager.getLastKnownLocation(mLocationManager.getBestProvider(crita, false));
        if (oldLocation != null) {
            return oldLocation.getLatitude()+","+oldLocation.getLongitude();
        }
        return Constants.INITIAL_LAT+","+Constants.INITIAL_LAT;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode==LAC_REA_PERMISSION&&grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            Criteria crit = new Criteria();
            crit.setAccuracy(Criteria.ACCURACY_FINE);


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FireManager.getRideNode()
                .child(Constants.RIDE_STATUS_KEY).setValue("");
        return true;
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            //your code here
            Log.d("Location", "onLocationChanged: "+location.getLatitude());
            HashMap map = new HashMap();
            map.put(Constants.DRIVER_LATLNG_KEY + "/lat", location.getLatitude());
            map.put(Constants.DRIVER_LATLNG_KEY + "/lng", location.getLongitude());
            FireManager.getRideNode()
                    .updateChildren(map);

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };
    private void sendLocationUpdates() {
        LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria crita = new Criteria();
        crita.setAccuracy(Criteria.ACCURACY_FINE);
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLocationManager.requestLocationUpdates(mLocationManager.getBestProvider(crita, false), 1000,
                0, mLocationListener);
    }
}
